package com.evento.backend.service;

import com.evento.backend.domain.Evento;
import com.evento.backend.domain.enumeration.EstadoAsiento;
import com.evento.backend.repository.EventoRepository;
import com.evento.backend.service.dto.AsientoDisponibilidadDTO;
import com.evento.backend.service.dto.MatrizAsientosDTO;
import com.evento.backend.service.dto.RedisAsientoDTO;
import com.evento.backend.service.dto.RedisResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
/**
 * Servicio de lógica de negocio para consultar disponibilidad de asientos
 *
 * Responsabilidades:
 * - Obtener información del evento desde BD local
 * - Consultar asientos bloqueados/vendidos desde Proxy (Redis cátedra)
 * - Construir matriz completa de asientos con estados
 * - Calcular estadísticas (disponibles, bloqueados, vendidos)
 */
@Service
@Transactional(readOnly = true)
public class AsientosDisponibilidadService {
    private static final Logger log = LoggerFactory.getLogger(AsientosDisponibilidadService.class);
    private final EventoRepository eventoRepository;
    private final ProxyClientService proxyClientService;

    public AsientosDisponibilidadService(
        EventoRepository eventoRepository,
        ProxyClientService proxyClientService
    ) {
        this.eventoRepository = eventoRepository;
        this.proxyClientService = proxyClientService;
    }

    /**
     * Obtiene la disponibilidad completa de asientos para un evento
     * <p>
     * Flujo:
     * 1. Buscar evento en BD local por ID
     * 2. Validar que evento esté activo y tenga configuración de asientos
     * 3. Consultar Proxy para obtener asientos bloqueados/vendidos de Redis
     * 4. Construir matriz completa (todos los asientos)
     * 5. Marcar estados según datos de Redis
     * 6. Calcular estadísticas
     *
     * @param eventoId - ID local del evento en BD Backend
     * @return MatrizAsientosDTO con información completa
     * @throws EntityNotFoundException si evento no existe
     * @throws IllegalStateException   si evento inactivo o sin configuración
     * @throws RuntimeException        si Proxy no disponible
     */
    public MatrizAsientosDTO getDisponibilidadAsientos(Long eventoId) {
        log.debug("=== INICIANDO CONSULTA DE DISPONIBILIDAD ===");
        log.debug("Evento ID local: {}", eventoId);
        // 1. Buscar evento en BD local
        Evento evento = eventoRepository.findById(eventoId)
            .orElseThrow(() -> {
                log.error("Evento no encontrado: {}", eventoId);
                return new EntityNotFoundException("Evento no encontrado: " + eventoId);
            });
        log.debug("Evento encontrado: {} (ID Cátedra: {})", evento.getTitulo(), evento.getIdCatedra());
        // 2. Validaciones
        if (Boolean.FALSE.equals(evento.getActivo())) {
            log.warn("Intento de consultar evento inactivo: {}", eventoId);
            throw new IllegalStateException("Evento inactivo: " + eventoId);
        }
        if (evento.getFilaAsientos() == null || evento.getColumnaAsientos() == null) {
            log.error("Evento sin configuración de asientos: {}", eventoId);
            throw new IllegalStateException("Evento sin configuración de asientos");
        }
        if (evento.getIdCatedra() == null) {
            log.error("Evento sin idCatedra: {}", eventoId);
            throw new IllegalStateException("Evento sin idCatedra (no sincronizado)");
        }
        log.debug("Validaciones OK - Filas: {}, Columnas: {}",
            evento.getFilaAsientos(), evento.getColumnaAsientos());
        // 3. Consultar asientos bloqueados/vendidos desde Proxy
        Optional<RedisResponseDTO> redisData = proxyClientService
            .consultarAsientosDesdeProxy(evento.getIdCatedra());
        if (redisData.isPresent()) {
            log.debug("Datos de Redis recibidos: {} asientos bloqueados/vendidos",
                redisData.get().getAsientos().size());
        } else {
            log.debug("Sin datos en Redis - todos los asientos disponibles");
        }
        // 4. Construir matriz completa
        MatrizAsientosDTO matriz = construirMatrizCompleta(evento, redisData);
        log.info("=== CONSULTA COMPLETADA ===");
        log.info("Evento: {} | Total: {} | Disponibles: {} | Bloqueados: {} | Vendidos: {}",
            evento.getTitulo(), matriz.getTotalAsientos(),
            matriz.getDisponibles(), matriz.getBloqueados(), matriz.getVendidos());
        return matriz;
    }

    /**
     * Construye la matriz completa de asientos con estados
     * <p>
     * Estrategia:
     * 1. Crear matriz inicializada con todos DISPONIBLE
     * 2. Si hay datos de Redis, actualizar estados (BLOQUEADO/VENDIDO)
     * 3. Validar expiración de bloqueos
     * 4. Calcular estadísticas
     */
    private MatrizAsientosDTO construirMatrizCompleta(
        Evento evento,
        Optional<RedisResponseDTO> redisData
    ) {
        int totalFilas = evento.getFilaAsientos();
        int totalColumnas = evento.getColumnaAsientos();
        int totalAsientos = totalFilas * totalColumnas;
        log.debug("Construyendo matriz: {}x{} = {} asientos",
            totalFilas, totalColumnas, totalAsientos);
        // 1. Crear lista de asientos inicializada como DISPONIBLE
        List<AsientoDisponibilidadDTO> asientos = new ArrayList<>(totalAsientos);
        for (int fila = 1; fila <= totalFilas; fila++) {
            for (int columna = 1; columna <= totalColumnas; columna++) {
                AsientoDisponibilidadDTO asiento = new AsientoDisponibilidadDTO(
                    fila, columna, EstadoAsiento.DISPONIBLE
                );
                asientos.add(asiento);
            }
        }
        // Contadores iniciales
        int disponibles = totalAsientos;
        int bloqueados = 0;
        int vendidos = 0;
        // 2. Si hay datos de Redis, actualizar estados
        if (redisData.isPresent() && redisData.get().getAsientos() != null) {
            log.debug("Actualizando estados desde Redis...");
            // Crear mapa para acceso rápido: "fila-columna" -> AsientoDTO
            Map<String, AsientoDisponibilidadDTO> mapaAsientos = new HashMap<>();
            for (AsientoDisponibilidadDTO asiento : asientos) {
                String key = asiento.getFila() + "-" + asiento.getColumna();
                mapaAsientos.put(key, asiento);
            }
            Instant ahora = Instant.now();
            // Procesar cada asiento de Redis
            for (RedisAsientoDTO redisAsiento : redisData.get().getAsientos()) {
                String key = redisAsiento.getFila() + "-" + redisAsiento.getColumna();
                AsientoDisponibilidadDTO asiento = mapaAsientos.get(key);
                if (asiento == null) {
                    log.warn("Asiento en Redis fuera de rango: fila={}, columna={}",
                        redisAsiento.getFila(), redisAsiento.getColumna());
                    continue;
                }
                String estado = redisAsiento.getEstado();
                if ("Bloqueado".equalsIgnoreCase(estado)) {
                    // Verificar si el bloqueo expiró
                    if (redisAsiento.getExpira() != null) {
                        if (redisAsiento.getExpira().isAfter(ahora)) {
                            // Bloqueo vigente
                            asiento.setEstado(EstadoAsiento.BLOQUEADO);
                            asiento.setExpira(redisAsiento.getExpira());
                            bloqueados++;
                            disponibles--;
                            log.trace("Asiento ({},{}) BLOQUEADO hasta {}",
                                redisAsiento.getFila(), redisAsiento.getColumna(),
                                redisAsiento.getExpira());
                        } else {
                            // Bloqueo expirado - queda como DISPONIBLE
                            log.trace("Asiento ({},{}) bloqueo EXPIRADO - disponible",
                                redisAsiento.getFila(), redisAsiento.getColumna());
                        }
                    }
                } else if ("Vendido".equalsIgnoreCase(estado)) {
                    // Asiento vendido
                    asiento.setEstado(EstadoAsiento.VENDIDO);
                    asiento.setNombrePersona(redisAsiento.getNombrePersona());
                    vendidos++;
                    disponibles--;
                    log.trace("Asiento ({},{}) VENDIDO",
                        redisAsiento.getFila(), redisAsiento.getColumna());
                }
            }
            log.debug("Estados actualizados: {} bloqueados, {} vendidos",
                bloqueados, vendidos);
        }
        // 3. Construir DTO de respuesta
        MatrizAsientosDTO response = new MatrizAsientosDTO();
        response.setEventoId(evento.getId());
        response.setEventoIdCatedra(evento.getIdCatedra());
        response.setTituloEvento(evento.getTitulo());
        response.setTotalFilas(totalFilas);
        response.setTotalColumnas(totalColumnas);
        response.setTotalAsientos(totalAsientos);
        response.setDisponibles(disponibles);
        response.setBloqueados(bloqueados);
        response.setVendidos(vendidos);
        response.setAsientos(asientos);
        response.setConsultadoEn(Instant.now());
        return response;
    }
}
