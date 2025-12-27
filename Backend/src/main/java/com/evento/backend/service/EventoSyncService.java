package com.evento.backend.service;

import com.evento.backend.domain.Evento;
import com.evento.backend.domain.EventoTipo;
import com.evento.backend.domain.Integrante;
import com.evento.backend.repository.EventoRepository;
import com.evento.backend.repository.EventoTipoRepository;
import com.evento.backend.repository.IntegranteRepository;
import com.evento.backend.service.dto.EventoCatedraDTO;
import com.evento.backend.service.dto.EventoTipoCatedraDTO;
import com.evento.backend.service.dto.IntegranteCatedraDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
/**
 * Servicio para sincronizar eventos desde el API de cátedra hacia la base de datos local.
 *
 * Responsabilidades:
 * - Obtener eventos completos desde CatedraApiClient
 * - Crear/actualizar eventos (UPSERT por idCatedra)
 * - Crear tipos de evento si no existen
 * - Sincronizar integrantes (delete + re-insert)
 * - Desactivar eventos que ya no existen en cátedra
 * - Retornar resumen de operaciones realizadas
 */
@Service
@Transactional
public class EventoSyncService {

    private static final Logger log = LoggerFactory.getLogger(EventoSyncService.class);

    private final CatedraApiClient catedraApiClient;
    private final EventoRepository eventoRepository;
    private final EventoTipoRepository eventoTipoRepository;
    private final IntegranteRepository integranteRepository;

    public EventoSyncService(
        CatedraApiClient catedraApiClient,
        EventoRepository eventoRepository,
        EventoTipoRepository eventoTipoRepository,
        IntegranteRepository integranteRepository
    ) {
        this.catedraApiClient = catedraApiClient;
        this.eventoRepository = eventoRepository;
        this.eventoTipoRepository = eventoTipoRepository;
        this.integranteRepository = integranteRepository;
    }

    /**
     * Ejecuta la sincronización completa de eventos desde el API de cátedra.
     *
     * Proceso:
     * 1. Obtener eventos desde CatedraApiClient
     * 2. Para cada evento:
     *    - Obtener/crear EventoTipo
     *    - UPSERT Evento (crear o actualizar)
     *    - Sincronizar Integrantes
     * 3. Desactivar eventos que ya no existen en cátedra
     * 4. Retornar resumen de operaciones
     *
     * Manejo de errores: Continúa procesando aunque falle un evento individual.
     *
     * @return SyncResult con contadores de operaciones y errores
     */
    public SyncResult synchronize() {
        log.info("=== INICIANDO SINCRONIZACIÓN DE EVENTOS ===");

        SyncResult result = new SyncResult();
        Set<Long> idsCatedraActivos = new HashSet<>();

        try {
            // PASO 1: Obtener eventos desde cátedra
            log.info("Consultando eventos desde API de cátedra...");
            List<EventoCatedraDTO> eventosCatedra = catedraApiClient.getEventosCompletos();

            if (eventosCatedra == null || eventosCatedra.isEmpty()) {
                log.warn("No se obtuvieron eventos desde cátedra (lista vacía o nula)");
                return result;
            }

            log.info("Eventos recibidos desde cátedra: {}", eventosCatedra.size());

            // PASO 2: Procesar cada evento individualmente
            for (EventoCatedraDTO eventoDto : eventosCatedra) {
                try {
                    log.debug("--- Procesando evento: idCatedra={}, titulo={}",
                        eventoDto.getId(), eventoDto.getTitulo());

                    // 2.1 Obtener o crear EventoTipo
                    EventoTipo tipo = findOrCreateEventoTipo(eventoDto.getEventoTipo());

                    // 2.2 Verificar si el evento ya existe (para saber si es create o update)
                    boolean existeAntes = eventoRepository.findByIdCatedra(eventoDto.getId()).isPresent();

                    // 2.3 UPSERT Evento (crear o actualizar)
                    Evento evento = upsertEvento(eventoDto, tipo);

                    // 2.4 Sincronizar Integrantes (delete + re-insert)
                    syncIntegrantes(evento, eventoDto.getIntegrantes());

                    // 2.5 Recordar este ID como activo (para desactivar faltantes después)
                    idsCatedraActivos.add(eventoDto.getId());

                    // 2.6 Actualizar contadores
                    if (existeAntes) {
                        result.incrementUpdated();
                    } else {
                        result.incrementCreated();
                    }

                } catch (Exception e) {
                    // Error procesando este evento individual - LOG y CONTINUAR con el siguiente
                    String errorMsg = String.format("Error procesando evento idCatedra=%d: %s",
                        eventoDto.getId(), e.getMessage());
                    log.error(errorMsg, e);
                    result.addError(errorMsg);
                }
            }

            // PASO 3: Desactivar eventos que ya no existen en cátedra
            log.info("Verificando eventos faltantes para desactivar...");
            int desactivados = deactivateMissingEventos(idsCatedraActivos);
            result.setDeactivated(desactivados);

        } catch (Exception e) {
            // Error crítico (ej: no se pudo conectar a cátedra)
            String errorMsg = "Error crítico durante sincronización: " + e.getMessage();
            log.error(errorMsg, e);
            result.addError(errorMsg);
        }

        // PASO 4: Log de resumen y retorno
        log.info("=== SINCRONIZACIÓN COMPLETADA ===");
        log.info("Resultados:");
        log.info("  - Eventos creados: {}", result.getCreated());
        log.info("  - Eventos actualizados: {}", result.getUpdated());
        log.info("  - Eventos desactivados: {}", result.getDeactivated());
        log.info("  - Errores: {}", result.getErrors().size());

        if (!result.getErrors().isEmpty()) {
            log.warn("Errores durante sincronización:");
            result.getErrors().forEach(err -> log.warn("  - {}", err));
        }

        return result;
    }

    private EventoTipo findOrCreateEventoTipo(EventoTipoCatedraDTO dto) {
        if (dto == null || dto.getNombre() == null) {
            throw new IllegalArgumentException("EventoTipoCatedraDTO o su nombre no pueden ser nulos");
        }

        // Buscar por nombre
        Optional<EventoTipo> tipoOpt = eventoTipoRepository.findByNombre(dto.getNombre());

        if (tipoOpt.isPresent()) {
            log.debug("EventoTipo encontrado: {}", dto.getNombre());
            return tipoOpt.get();
        }

        // Si no existe, crear nuevo
        log.info("Creando nuevo EventoTipo: {}", dto.getNombre());
        EventoTipo nuevoTipo = new EventoTipo();
        nuevoTipo.setNombre(dto.getNombre());
        nuevoTipo.setDescripcion(dto.getDescripcion());

        return eventoTipoRepository.save(nuevoTipo);
    }

    private Evento upsertEvento(EventoCatedraDTO dto, EventoTipo tipo) {
        // Buscar evento existente por idCatedra
        Optional<Evento> eventoOpt = eventoRepository.findByIdCatedra(dto.getId());

        Evento evento;
        boolean esNuevo = false;

        if (eventoOpt.isPresent()) {
            // ACTUALIZAR evento existente
            evento = eventoOpt.get();
            log.debug("Actualizando evento existente: idCatedra={}, titulo={}",
                dto.getId(), dto.getTitulo());
        } else {
            // CREAR nuevo evento
            evento = new Evento();
            evento.setIdCatedra(dto.getId());
            esNuevo = true;
            log.debug("Creando nuevo evento: idCatedra={}, titulo={}",
                dto.getId(), dto.getTitulo());
        }

        // COPIAR todos los campos desde el DTO (tanto para nuevo como actualizado)
        evento.setTitulo(dto.getTitulo());
        evento.setDescripcion(dto.getDescripcion());
        evento.setFecha(dto.getFecha());
        evento.setImagen(dto.getImagen());
        evento.setFilaAsientos(dto.getFilaAsientos());
        evento.setColumnaAsientos(dto.getColumnaAsientos());
        evento.setPrecioEntrada(dto.getPrecioEntrada());
        evento.setActivo(true);
        evento.setEventoTipo(tipo);

        // Guardar en base de datos
        Evento eventoGuardado = eventoRepository.save(evento);

        if (esNuevo) {
            log.info("Evento creado: ID={}, idCatedra={}, titulo='{}'",
                eventoGuardado.getId(), dto.getId(), dto.getTitulo());
        } else {
            log.debug("Evento actualizado: ID={}, idCatedra={}",
                eventoGuardado.getId(), dto.getId());
        }

        return eventoGuardado;
    }

    private void syncIntegrantes(Evento evento, List<IntegranteCatedraDTO> dtos) {
        // 1. BORRAR todos los integrantes existentes del evento
        integranteRepository.deleteByEvento(evento);
        log.debug("  → Integrantes anteriores eliminados");

        // 2. CREAR nuevos integrantes desde los DTOs
        if (dtos != null && !dtos.isEmpty()) {
            for (IntegranteCatedraDTO dto : dtos) {
                Integrante integrante = new Integrante();
                integrante.setNombre(dto.getNombre());
                integrante.setApellido(dto.getApellido());
                integrante.setIdentificacion(dto.getIdentificacion());
                integrante.setEvento(evento);

                integranteRepository.save(integrante);
            }
            log.debug("  → {} integrantes sincronizados", dtos.size());
        } else {
            log.debug("  → Sin integrantes para este evento");
        }
    }

    private int deactivateMissingEventos(Set<Long> idsCatedraActivos) {
        // Buscar todos los eventos en la base de datos
        List<Evento> todosEventos = eventoRepository.findAll();

        int desactivados = 0;

        for (Evento evento : todosEventos) {
            // Solo procesar eventos que:
            // 1. Tienen idCatedra (vinieron de cátedra originalmente)
            // 2. NO están en la lista de activos actual
            // 3. Están marcados como activo=true (para no procesar los ya inactivos)
            if (evento.getIdCatedra() != null
                && !idsCatedraActivos.contains(evento.getIdCatedra())
                && evento.getActivo() != null
                && evento.getActivo()) {

                log.info("Desactivando evento faltante: ID={}, idCatedra={}, titulo='{}'",
                    evento.getId(), evento.getIdCatedra(), evento.getTitulo());

                evento.setActivo(false);
                eventoRepository.save(evento);
                desactivados++;
            }
        }

        if (desactivados > 0) {
            log.info("Total de eventos desactivados: {}", desactivados);
        }

        return desactivados;
    }


    public static class SyncResult {
        private int created = 0;
        private int updated = 0;
        private int deactivated = 0;
        private List<String> errors = new ArrayList<>();

        public SyncResult() {}

        // Getters y Setters
        public int getCreated() {
            return created;
        }

        public void setCreated(int created) {
            this.created = created;
        }

        public int getUpdated() {
            return updated;
        }

        public void setUpdated(int updated) {
            this.updated = updated;
        }

        public int getDeactivated() {
            return deactivated;
        }

        public void setDeactivated(int deactivated) {
            this.deactivated = deactivated;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }

        public void addError(String error) {
            this.errors.add(error);
        }

        public void incrementCreated() {
            this.created++;
        }

        public void incrementUpdated() {
            this.updated++;
        }
    }
}

