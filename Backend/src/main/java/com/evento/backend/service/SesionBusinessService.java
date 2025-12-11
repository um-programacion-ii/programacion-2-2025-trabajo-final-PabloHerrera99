package com.evento.backend.service;

import com.evento.backend.domain.*;
import com.evento.backend.domain.enumeration.*;
import com.evento.backend.repository.*;
import com.evento.backend.service.dto.*;
import com.evento.backend.service.mapper.SesionMapper;
import com.evento.backend.service.mapper.VentaMapper;
import com.evento.backend.web.rest.errors.BadRequestAlertException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de lógica de negocio para gestión de sesiones de compra.
 * Maneja el flujo completo: inicio sesión → selección asientos → asignación nombres → venta.
 */
@Service
@Transactional
public class SesionBusinessService {
    private static final Logger log = LoggerFactory.getLogger(SesionBusinessService.class);

    // Constantes de negocio
    private static final int MAX_ASIENTOS_POR_COMPRA = 4;
    private static final int MIN_LONGITUD_NOMBRE = 3;

    // Redis
    private final SesionRedisService sesionRedisService;

    // Repositories JPA
    private final SesionRepository sesionRepository;
    private final EventoRepository eventoRepository;
    private final AsientoSeleccionadoRepository asientoSeleccionadoRepository;
    private final UserRepository userRepository;
    private final VentaRepository ventaRepository;
    private final AsientoVendidoRepository asientoVendidoRepository;

    // Servicios
    private final AsientosDisponibilidadService asientosDisponibilidadService;
    private final CatedraClientService catedraClientService;

    // Mappers
    private final SesionMapper sesionMapper;
    private final VentaMapper ventaMapper;
    public SesionBusinessService(
        SesionRedisService sesionRedisService,
        SesionRepository sesionRepository,
        EventoRepository eventoRepository,
        AsientoSeleccionadoRepository asientoSeleccionadoRepository,
        UserRepository userRepository,
        VentaRepository ventaRepository,
        AsientoVendidoRepository asientoVendidoRepository,
        AsientosDisponibilidadService asientosDisponibilidadService,
        CatedraClientService catedraClientService,
        SesionMapper sesionMapper,
        VentaMapper ventaMapper
    ) {
        this.sesionRedisService = sesionRedisService;
        this.sesionRepository = sesionRepository;
        this.eventoRepository = eventoRepository;
        this.asientoSeleccionadoRepository = asientoSeleccionadoRepository;
        this.userRepository = userRepository;
        this.ventaRepository = ventaRepository;
        this.asientoVendidoRepository = asientoVendidoRepository;
        this.asientosDisponibilidadService = asientosDisponibilidadService;
        this.catedraClientService = catedraClientService;
        this.sesionMapper = sesionMapper;
        this.ventaMapper = ventaMapper;
    }
    // ========== MÉTODOS PÚBLICOS ==========
    /**
     * Inicia una nueva sesión de compra para un usuario y evento.
     * Si el usuario ya tiene una sesión activa, la cancela automáticamente.
     *
     * @param userId ID del usuario
     * @param eventoId ID del evento
     * @return DTO de la sesión creada
     */
    public SesionDTO iniciarSesion(Long userId, Long eventoId) {
        log.debug("Iniciando sesión para usuario {} en evento {}", userId, eventoId);
        // 1. Validar usuario existe
        User user = userRepository
            .findById(userId)
            .orElseThrow(() -> new BadRequestAlertException("Usuario no encontrado", "sesion", "usernotfound"));
        // 2. Validar evento existe
        Evento evento = eventoRepository
            .findById(eventoId)
            .orElseThrow(() -> new BadRequestAlertException("Evento no encontrado", "sesion", "eventonotfound"));
        // 3. Buscar sesión activa previa del usuario
        Optional<Sesion> sesionActivaOpt = sesionRepository.findFirstByUsuarioIdAndEstadoNot(userId, EstadoSesion.COMPLETADO);
        // 4. Si existe sesión previa, cancelarla
        if (sesionActivaOpt.isPresent()) {
            Sesion sesionPrevia = sesionActivaOpt.get();
            log.info("Cancelando sesión previa {} del usuario {}", sesionPrevia.getId(), userId);
            // Eliminar asientos seleccionados
            asientoSeleccionadoRepository.deleteBySesionId(sesionPrevia.getId());
            // Eliminar de Redis
            sesionRedisService.eliminarSesion(userId.toString());
            // Marcar como completada
            sesionPrevia.setEstado(EstadoSesion.COMPLETADO);
            sesionRepository.save(sesionPrevia);
        }
        // 5. Crear nueva sesión
        Instant ahora = Instant.now();
        Sesion nuevaSesion = new Sesion();
        nuevaSesion.setUsuario(user);
        nuevaSesion.setEvento(evento);
        nuevaSesion.setEstado(EstadoSesion.SELECCION_ASIENTOS);
        nuevaSesion.setFechaInicio(ahora);
        nuevaSesion.setUltimaActividad(ahora);
        nuevaSesion.setExpiracion(ahora.plusSeconds(30 * 60)); // TTL 30 minutos
        nuevaSesion.setActiva(true);
        // 6. Guardar en PostgreSQL
        nuevaSesion = sesionRepository.save(nuevaSesion);
        // 7. Guardar en Redis
        SesionDTO sesionDTO = sesionMapper.toDto(nuevaSesion);

        // Convertir SesionDTO a SesionRedisDTO
        SesionRedisDTO sesionRedisDTO = new SesionRedisDTO();
        sesionRedisDTO.setSesionId(sesionDTO.getId());
        sesionRedisDTO.setEstado(sesionDTO.getEstado());
        sesionRedisDTO.setUltimaActividad(sesionDTO.getUltimaActividad());
        sesionRedisDTO.setExpiracion(Instant.now().plusSeconds(30 * 60)); // TTL 30 minutos
        if (sesionDTO.getEvento() != null) {
            sesionRedisDTO.setEventoId(sesionDTO.getEvento().getId());
        }
        if (sesionDTO.getUsuario() != null) {
            sesionRedisDTO.setUserId(userId.toString());
        }

        sesionRedisService.guardarSesion(userId.toString(), sesionRedisDTO);
        log.info("Sesión {} iniciada exitosamente para usuario {} en evento {}", nuevaSesion.getId(), userId, eventoId);
        return sesionDTO;
    }
    /**
     * Obtiene el estado actual de la sesión de un usuario.
     * Consulta primero Redis, si no existe intenta rehidratar desde PostgreSQL.
     *
     * @param userId ID del usuario
     * @return DTO de la sesión o null si no existe
     */
    public SesionDTO obtenerEstadoSesion(Long userId) {
        log.debug("Obteniendo estado de sesión para usuario {}", userId);
        // 1. Intentar obtener desde Redis
        Optional<SesionRedisDTO> sesionRedisOpt = sesionRedisService.obtenerSesion(userId.toString());
        if (sesionRedisOpt.isPresent()) {
            log.debug("Sesión encontrada en Redis para usuario {}", userId);

            // Buscar en PostgreSQL para obtener datos completos
            SesionRedisDTO sesionRedis = sesionRedisOpt.get();
            if (sesionRedis.getSesionId() == null) {
                log.warn("Sesión en Redis sin ID persistente para usuario {}", userId);
                return null;
            }
            Optional<Sesion> sesionOpt = sesionRepository.findById(sesionRedis.getSesionId());

            return sesionOpt.map(sesionMapper::toDto).orElse(null);
        }
        // 2. Si no está en Redis, buscar en PostgreSQL
        Optional<Sesion> sesionOpt = sesionRepository.findFirstByUsuarioIdAndEstadoNot(userId, EstadoSesion.COMPLETADO);
        if (sesionOpt.isEmpty()) {
            log.debug("No existe sesión activa para usuario {}", userId);
            return null;
        }
        Sesion sesion = sesionOpt.get();
        // 3. Verificar si expiró (más de 30 minutos de inactividad)
        Instant limiteExpiracion = Instant.now().minusSeconds(30 * 60);
        if (sesion.getUltimaActividad().isBefore(limiteExpiracion)) {
            log.info("Sesión {} del usuario {} expiró, marcando como completada", sesion.getId(), userId);
            // Marcar como completada
            sesion.setEstado(EstadoSesion.COMPLETADO);
            sesionRepository.save(sesion);
            // Limpiar asientos
            asientoSeleccionadoRepository.deleteBySesionId(sesion.getId());
            return null;
        }
        // 4. Rehidratar Redis
        SesionDTO sesionDTO = sesionMapper.toDto(sesion);

        SesionRedisDTO sesionRedisDTO = new SesionRedisDTO();
        sesionRedisDTO.setSesionId(sesionDTO.getId());
        sesionRedisDTO.setEstado(sesionDTO.getEstado());
        sesionRedisDTO.setUltimaActividad(sesionDTO.getUltimaActividad());
        sesionRedisDTO.setExpiracion(Instant.now().plusSeconds(30 * 60)); // TTL 30 minutos
        if (sesionDTO.getEvento() != null) {
            sesionRedisDTO.setEventoId(sesionDTO.getEvento().getId());
        }
        if (sesionDTO.getUsuario() != null) {
            sesionRedisDTO.setUserId(userId.toString());
        }

        sesionRedisService.guardarSesion(userId.toString(), sesionRedisDTO);
        log.debug("Sesión {} rehidratada en Redis para usuario {}", sesion.getId(), userId);
        return sesionDTO;
    }
    /**
     * Actualiza la actividad de la sesión (keep-alive).
     * Usado para evitar expiración por inactividad.
     *
     * @param userId ID del usuario
     */
    public void actualizarActividad(Long userId) {
        log.debug("Actualizando actividad para usuario {}", userId);
        // 1. Verificar que existe en Redis
        if (!sesionRedisService.existeSesion(userId.toString())) {
            throw new BadRequestAlertException("No existe sesión activa", "sesion", "nosession");
        }
        // 2. Actualizar TTL en Redis
        sesionRedisService.actualizarActividad(userId.toString());
        // 3. Actualizar en PostgreSQL
        Optional<Sesion> sesionOpt = sesionRepository.findFirstByUsuarioIdAndEstadoNot(userId, EstadoSesion.COMPLETADO);
        if (sesionOpt.isPresent()) {
            Sesion sesion = sesionOpt.get();
            sesion.setUltimaActividad(Instant.now());
            sesionRepository.save(sesion);
            log.debug("Actividad actualizada para sesión {}", sesion.getId());
        }
    }
    /**
     * Cancela la sesión activa de un usuario.
     * Marca la sesión como completada y elimina asientos seleccionados.
     *
     * @param userId ID del usuario
     */
    public void cancelarSesion(Long userId) {
        log.debug("Cancelando sesión para usuario {}", userId);
        // 1. Buscar sesión en PostgreSQL
        Optional<Sesion> sesionOpt = sesionRepository.findFirstByUsuarioIdAndEstadoNot(userId, EstadoSesion.COMPLETADO);
        if (sesionOpt.isPresent()) {
            Sesion sesion = sesionOpt.get();
            // 2. Marcar como completada
            sesion.setEstado(EstadoSesion.COMPLETADO);
            sesionRepository.save(sesion);
            // 3. Eliminar asientos seleccionados
            asientoSeleccionadoRepository.deleteBySesionId(sesion.getId());
            log.info("Sesión {} cancelada para usuario {}", sesion.getId(), userId);
        }
        // 4. Eliminar de Redis (si existe)
        sesionRedisService.eliminarSesion(userId.toString());
        log.debug("Sesión cancelada exitosamente para usuario {}", userId);
    }
    // ========== MÉTODOS DE SELECCIÓN Y ASIGNACIÓN ==========
    /**
     * Selecciona asientos para la sesión activa del usuario.
     * Valida disponibilidad y bloquea en el servidor de cátedra.
     *
     * @param userId ID del usuario
     * @param asientos Lista de asientos a seleccionar
     * @return DTO de la sesión actualizada
     */
    public SesionDTO seleccionarAsientos(Long userId, List<AsientoSimpleDTO> asientos) {
        log.debug("Seleccionando {} asientos para usuario {}", asientos.size(), userId);
        // 1. Validar lista no vacía y límite máximo
        if (asientos == null || asientos.isEmpty()) {
            throw new BadRequestAlertException("Debe seleccionar al menos un asiento", "sesion", "noasientos");
        }
        if (asientos.size() > MAX_ASIENTOS_POR_COMPRA) {
            throw new BadRequestAlertException(
                "Máximo " + MAX_ASIENTOS_POR_COMPRA + " asientos por compra",
                "sesion",
                "maxasientos"
            );
        }
        // 2. Validar no hay duplicados
        Set<String> asientosSet = new HashSet<>();
        for (AsientoSimpleDTO asiento : asientos) {
            String key = asiento.getFila() + "-" + asiento.getColumna();
            if (!asientosSet.add(key)) {
                throw new BadRequestAlertException(
                    "Asiento duplicado: fila " + asiento.getFila() + ", columna " + asiento.getColumna(),
                    "sesion",
                    "duplicateasiento"
                );
            }
        }
        // 3. Obtener sesión desde Redis
        Optional<SesionRedisDTO> sesionRedisOpt = sesionRedisService.obtenerSesion(userId.toString());
        if (sesionRedisOpt.isEmpty()) {
            throw new BadRequestAlertException("No existe sesión activa", "sesion", "nosession");
        }
        SesionRedisDTO sesionRedis = sesionRedisOpt.get();

        // Obtener sesión completa de PostgreSQL
        Sesion sesion = sesionRepository
            .findById(sesionRedis.getSesionId())
            .orElseThrow(() -> new BadRequestAlertException("Sesión no encontrada", "sesion", "sessionnotfound"));
        // 4. Validar estado permite selección
        if (sesion.getEstado() != EstadoSesion.SELECCION_ASIENTOS && sesion.getEstado() != EstadoSesion.CARGA_DATOS) {
            throw new BadRequestAlertException(
                "Estado de sesión no permite selección de asientos",
                "sesion",
                "invalidstate"
            );
        }
        // 5. Obtener evento desde PostgreSQL
        Evento evento = sesion.getEvento();
        // 6. Validar coordenadas dentro de rango
        for (AsientoSimpleDTO asiento : asientos) {
            validarCoordenadasAsiento(evento, asiento.getFila(), asiento.getColumna());
        }
        // 7. Consultar disponibilidad desde Redis cátedra
        MatrizAsientosDTO matrizDisponibilidad = asientosDisponibilidadService.getDisponibilidadAsientos(
            evento.getId()
        );
        List<AsientoDisponibilidadDTO> listaAsientos = matrizDisponibilidad.getAsientos();
        // 8. Verificar TODOS los asientos están disponibles
        for (AsientoSimpleDTO asiento : asientos) {
            AsientoDisponibilidadDTO asientoDisp = buscarAsientoEnLista(
                listaAsientos,
                asiento.getFila(),
                asiento.getColumna()
            );
            if (asientoDisp.getEstado() != EstadoAsiento.DISPONIBLE) {
                throw new BadRequestAlertException(
                    "Asiento no disponible: fila " + asiento.getFila() + ", columna " + asiento.getColumna() +
                        " (estado: " + asientoDisp.getEstado() + ")",
                    "sesion",
                    "asientonotavailable"
                );
            }
        }
        // 9. Bloquear en servidor de cátedra
        BloquearAsientosResponseDTO bloqueoResponse = catedraClientService.bloquearAsientos(
            evento.getIdCatedra(),
            asientos
        );
        // 11. Validar respuesta de bloqueo
        if (Boolean.FALSE.equals(bloqueoResponse.getResultado())) {
            throw new BadRequestAlertException(
                "No se pudieron bloquear los asientos: " + bloqueoResponse.getDescripcion(),
                "sesion",
                "bloqueofallido"
            );
        }
        // Verificar que TODOS los asientos quedaron bloqueados
        for (AsientoBloqueoResponseDTO asientoResp : bloqueoResponse.getAsientos()) {
            if (!"Bloqueo exitoso".equalsIgnoreCase(asientoResp.getEstado())) {
                throw new BadRequestAlertException(
                    "Asiento no pudo ser bloqueado: fila " + asientoResp.getFila() + ", columna " + asientoResp.getColumna(),
                    "sesion",
                    "asientonotbloqueado"
                );
            }
        }
        // 12. Eliminar asientos previos de la sesión (permitir re-selección)
        asientoSeleccionadoRepository.deleteBySesionId(sesion.getId());
        // 13. Crear nuevos AsientoSeleccionado
        List<AsientoSeleccionado> nuevosAsientos = new ArrayList<>();
        for (AsientoSimpleDTO asiento : asientos) {
            AsientoSeleccionado nuevoAsiento = new AsientoSeleccionado();
            nuevoAsiento.setSesion(sesion);
            nuevoAsiento.setFila(asiento.getFila());
            nuevoAsiento.setColumna(asiento.getColumna());
            nuevoAsiento.setNombrePersona(null); // Se asignará después
            nuevosAsientos.add(nuevoAsiento);
        }
        asientoSeleccionadoRepository.saveAll(nuevosAsientos);
        // 14. Actualizar estado sesión a CARGA_DATOS
        sesion.setEstado(EstadoSesion.CARGA_DATOS);
        sesion.setUltimaActividad(Instant.now());
        sesion = sesionRepository.save(sesion);
        // 15. Actualizar en Redis
        SesionDTO sesionActualizada = sesionMapper.toDto(sesion);

        SesionRedisDTO sesionRedisActualizada = new SesionRedisDTO();
        sesionRedisActualizada.setSesionId(sesionActualizada.getId());
        sesionRedisActualizada.setEstado(sesionActualizada.getEstado());
        sesionRedisActualizada.setExpiracion(Instant.now().plusSeconds(30 * 60));
        sesionRedisActualizada.setUltimaActividad(sesionActualizada.getUltimaActividad());
        if (sesionActualizada.getEvento() != null) {
            sesionRedisActualizada.setEventoId(sesionActualizada.getEvento().getId());
        }
        if (sesionActualizada.getUsuario() != null) {
            sesionRedisActualizada.setUserId(sesionActualizada.getUsuario().getId().toString());
        }

        sesionRedisService.guardarSesion(userId.toString(), sesionRedisActualizada);
        log.info("Asientos seleccionados exitosamente para sesión {}: {} asientos", sesion.getId(), asientos.size());
        return sesionActualizada;
    }
    /**
     * Asigna nombres a los asientos seleccionados.
     *
     * @param userId ID del usuario
     *  @param nombresAsignados Map de nombres por asiento (key: "fila-columna", value: nombre)
     * @return DTO de la sesión actualizada
     */
    public SesionDTO asignarNombres(Long userId, Map<String, String> nombresAsignados) {
        log.debug("Asignando nombres a asientos para usuario {}", userId);
        // 1. Obtener sesión desde Redis
        Optional<SesionRedisDTO> sesionRedisOpt = sesionRedisService.obtenerSesion(userId.toString());
        if (sesionRedisOpt.isEmpty()) {
            throw new BadRequestAlertException("No existe sesión activa", "sesion", "nosession");
        }
        SesionRedisDTO sesionRedis = sesionRedisOpt.get();

        // Obtener sesión completa de PostgreSQL
        Sesion sesion = sesionRepository
            .findById(sesionRedis.getSesionId())
            .orElseThrow(() -> new BadRequestAlertException("Sesión no encontrada", "sesion", "sessionnotfound"));
        // 2. Validar estado
        if (sesion.getEstado() != EstadoSesion.CARGA_DATOS) {
            throw new BadRequestAlertException(
                "Debe seleccionar asientos antes de asignar nombres",
                "sesion",
                "invalidstate"
            );
        }
        // 3. Obtener asientos de la sesión desde PostgreSQL
        List<AsientoSeleccionado> asientosSeleccionados = asientoSeleccionadoRepository.findBySesionId(sesion.getId());
        if (asientosSeleccionados.isEmpty()) {
            throw new BadRequestAlertException("No hay asientos seleccionados", "sesion", "noasientos");
        }
        // 4. Validar cantidad coincide
        if (nombresAsignados.size() != asientosSeleccionados.size()) {
            throw new BadRequestAlertException(
                "Debe asignar nombres a todos los asientos seleccionados",
                "sesion",
                "incompletenombres"
            );
        }
        // 5. Validar y asignar nombres desde Map
        for (AsientoSeleccionado asiento : asientosSeleccionados) {
            String key = asiento.getFila() + "-" + asiento.getColumna();
            String nombre = nombresAsignados.get(key);

            // Validar que existe nombre para este asiento
            if (nombre == null) {
                throw new BadRequestAlertException(
                    "Falta nombre para asiento: fila " + asiento.getFila() + ", columna " + asiento.getColumna(),
                    "sesion",
                    "faltanombre"
                );
            }

            // Validar longitud del nombre
            if (nombre.trim().length() < MIN_LONGITUD_NOMBRE) {
                throw new BadRequestAlertException(
                    "El nombre debe tener al menos " + MIN_LONGITUD_NOMBRE + " caracteres",
                    "sesion",
                    "nombreinvalido"
                );
            }

            // Asignar nombre
            asiento.setNombrePersona(nombre.trim());
        }
        // 6. Guardar cambios
        asientoSeleccionadoRepository.saveAll(asientosSeleccionados);
        // 7. Actualizar actividad en sesión
        sesion.setUltimaActividad(Instant.now());
        sesion = sesionRepository.save(sesion);
        // 8. Actualizar Redis
        SesionDTO sesionActualizada = sesionMapper.toDto(sesion);

        SesionRedisDTO sesionRedisActualizada = new SesionRedisDTO();
        sesionRedisActualizada.setSesionId(sesionActualizada.getId());
        sesionRedisActualizada.setEstado(sesionActualizada.getEstado());
        sesionRedisActualizada.setExpiracion(Instant.now().plusSeconds(30 * 60));
        sesionRedisActualizada.setUltimaActividad(sesionActualizada.getUltimaActividad());
        if (sesionActualizada.getEvento() != null) {
            sesionRedisActualizada.setEventoId(sesionActualizada.getEvento().getId());
        }
        if (sesionActualizada.getUsuario() != null) {
            sesionRedisActualizada.setUserId(sesionActualizada.getUsuario().getId().toString());
        }

        sesionRedisService.guardarSesion(userId.toString(), sesionRedisActualizada);
        log.info("Nombres asignados exitosamente para sesión {}", sesion.getId());
        return sesionActualizada;
    }
    /**
     * Confirma la venta de los asientos seleccionados.
     * Permite múltiples re-intentos si la venta falla.
     *
     * @param userId ID del usuario
     * @return DTO de la venta realizada
     */
    public VentaDTO confirmarVenta(Long userId) {
        log.debug("Confirmando venta para usuario {}", userId);
        // 1. Obtener sesión desde Redis
        Optional<SesionRedisDTO> sesionRedisOpt = sesionRedisService.obtenerSesion(userId.toString());
        if (sesionRedisOpt.isEmpty()) {
            throw new BadRequestAlertException("No existe sesión activa", "sesion", "nosession");
        }
        SesionRedisDTO sesionRedis = sesionRedisOpt.get();

        // Obtener sesión completa de PostgreSQL
        Sesion sesion = sesionRepository
            .findById(sesionRedis.getSesionId())
            .orElseThrow(() -> new BadRequestAlertException("Sesión no encontrada", "sesion", "sessionnotfound"));
        // 2. Validar estado
        if (sesion.getEstado() != EstadoSesion.CARGA_DATOS) {
            throw new BadRequestAlertException(
                "Debe asignar nombres a los asientos antes de confirmar",
                "sesion",
                "invalidstate"
            );
        }
        // 3. Obtener asientos seleccionados desde PostgreSQL
        List<AsientoSeleccionado> asientosSeleccionados = asientoSeleccionadoRepository.findBySesionId(sesion.getId());
        if (asientosSeleccionados.isEmpty()) {
            throw new BadRequestAlertException("No hay asientos seleccionados", "sesion", "noasientos");
        }
        // 4. Validar que TODOS tienen nombre asignado
        for (AsientoSeleccionado asiento : asientosSeleccionados) {
            if (!validarNombreCompleto(asiento.getNombrePersona())) {
                throw new BadRequestAlertException(
                    "Asiento sin nombre: fila " + asiento.getFila() + ", columna " + asiento.getColumna(),
                    "sesion",
                    "nombrefaltante"
                );
            }
        }
        // 5. Obtener evento
        Evento evento = sesion.getEvento();
        // 6. Calcular precio total (CORREGIDO: usar BigDecimal)
        BigDecimal precioTotal = evento.getPrecioEntrada().multiply(BigDecimal.valueOf(asientosSeleccionados.size()));
        // 7. Re-validar disponibilidad en Redis cátedra
        MatrizAsientosDTO matrizDisponibilidad = asientosDisponibilidadService.getDisponibilidadAsientos(
            evento.getId()
        );
        List<AsientoDisponibilidadDTO> listaAsientos = matrizDisponibilidad.getAsientos();
        List<AsientoSimpleDTO> asientosParaReBloquear = new ArrayList<>();
        for (AsientoSeleccionado asiento : asientosSeleccionados) {
            AsientoDisponibilidadDTO asientoDisp = buscarAsientoEnLista(
                listaAsientos,
                asiento.getFila(),
                asiento.getColumna()
            );
            // Verificar que no fue vendido por otro usuario
            if (asientoDisp.getEstado() == EstadoAsiento.VENDIDO) {
                throw new BadRequestAlertException(
                    "Asiento ya vendido: fila " + asiento.getFila() + ", columna " + asiento.getColumna(),
                    "sesion",
                    "asientovendido"
                );
            }
            // Si está DISPONIBLE (bloqueo expiró), necesita re-bloqueo
            if (asientoDisp.getEstado() == EstadoAsiento.DISPONIBLE) {
                asientosParaReBloquear.add(new AsientoSimpleDTO(asiento.getFila(), asiento.getColumna()));
            }
        }
        // 8. Re-bloquear si es necesario
        if (!asientosParaReBloquear.isEmpty()) {
            log.info("Re-bloqueando {} asientos cuyo bloqueo expiró", asientosParaReBloquear.size());
            BloquearAsientosResponseDTO reBloqueoResponse = catedraClientService.bloquearAsientos(
                evento.getIdCatedra(),
                asientosParaReBloquear
            );
            if (Boolean.FALSE.equals(reBloqueoResponse.getResultado())) {
                throw new BadRequestAlertException(
                    "No se pudo re-bloquear asientos: " + reBloqueoResponse.getDescripcion(),
                    "sesion",
                    "rebloqueofallido"
                );
            }
        }
        // 9. Construir request de venta para cátedra
        RealizarVentaRequestDTO ventaRequest = new RealizarVentaRequestDTO();
        ventaRequest.setEventoId(evento.getIdCatedra());
        ventaRequest.setFecha(Instant.now());
        ventaRequest.setPrecioVenta(precioTotal);
        List<AsientoVentaDTO> asientosVenta = new ArrayList<>();
        for (AsientoSeleccionado asiento : asientosSeleccionados) {
            AsientoVentaDTO asientoVentaDTO = new AsientoVentaDTO();
            asientoVentaDTO.setFila(asiento.getFila());
            asientoVentaDTO.setColumna(asiento.getColumna());
            asientoVentaDTO.setPersona(asiento.getNombrePersona());
            asientosVenta.add(asientoVentaDTO);
        }
        ventaRequest.setAsientos(asientosVenta);
        // 10. Realizar venta en servidor de cátedra
        RealizarVentaResponseDTO ventaResponse;
        try {
            ventaResponse = catedraClientService.realizarVenta(ventaRequest);
        } catch (Exception e) {
            // Error de comunicación (timeout, 500, etc.)
            log.error("Error de comunicación al realizar venta: {}", e.getMessage());
            // Crear venta con estado PENDIENTE (permite re-intento)
            Venta ventaPendiente = new Venta();
            ventaPendiente.setUsuario(sesion.getUsuario());
            ventaPendiente.setEvento(evento);
            ventaPendiente.setIdVentaCatedra(null);
            ventaPendiente.setFechaVenta(Instant.now());
            ventaPendiente.setPrecioTotal(precioTotal);
            ventaPendiente.setExitosa(false);
            ventaPendiente.setEstadoSincronizacion(EstadoSincronizacion.PENDIENTE);
            ventaRepository.save(ventaPendiente);
            throw new BadRequestAlertException(
                "Servidor de cátedra no disponible. Por favor, reintente en unos momentos.",
                "venta",
                "catedradown"
            );
        }
        // 11. Procesar respuesta de venta
        if (Boolean.TRUE.equals(ventaResponse.getResultado())) {
            // ===== CASO A: VENTA EXITOSA =====
            log.info("Venta exitosa en cátedra con ID: {}", ventaResponse.getVentaId());
            // Crear Venta entity
            Venta venta = new Venta();
            venta.setUsuario(sesion.getUsuario());
            venta.setEvento(evento);
            venta.setIdVentaCatedra(ventaResponse.getVentaId());
            venta.setFechaVenta(Instant.now());
            venta.setPrecioTotal(precioTotal);
            venta.setExitosa(true);
            venta.setEstadoSincronizacion(EstadoSincronizacion.SINCRONIZADA);
            venta = ventaRepository.save(venta);
            // Crear AsientoVendido entities
            List<AsientoVendido> asientosVendidos = new ArrayList<>();
            for (AsientoVentaResponseDTO asientoResp : ventaResponse.getAsientos()) {
                AsientoVendido asientoVendido = new AsientoVendido();
                asientoVendido.setVenta(venta);
                asientoVendido.setFila(asientoResp.getFila());
                asientoVendido.setColumna(asientoResp.getColumna());
                asientoVendido.setNombrePersona(asientoResp.getPersona());
                asientosVendidos.add(asientoVendido);
            }
            asientoVendidoRepository.saveAll(asientosVendidos);
            // Actualizar sesión a COMPLETADO
            sesion.setEstado(EstadoSesion.COMPLETADO);
            sesionRepository.save(sesion);
            // Eliminar de Redis
            sesionRedisService.eliminarSesion(userId.toString());
            // Eliminar asientos seleccionados
            asientoSeleccionadoRepository.deleteBySesionId(sesion.getId());
            log.info("Venta confirmada exitosamente: ID cátedra {}, ID local {}", ventaResponse.getVentaId(), venta.getId());
            // Retornar VentaDTO usando mapper
            return ventaMapper.toDto(venta);
        } else {
            // ===== CASO B: VENTA RECHAZADA POR CÁTEDRA =====
            log.warn("Venta rechazada por cátedra: {}", ventaResponse.getDescripcion());
            // Crear Venta con estado ERROR (permite re-intento)
            Venta ventaFallida = new Venta();
            ventaFallida.setUsuario(sesion.getUsuario());
            ventaFallida.setEvento(evento);
            ventaFallida.setIdVentaCatedra(null);
            ventaFallida.setFechaVenta(Instant.now());
            ventaFallida.setPrecioTotal(precioTotal);
            ventaFallida.setExitosa(false);
            ventaFallida.setEstadoSincronizacion(EstadoSincronizacion.ERROR);
            ventaRepository.save(ventaFallida);
            // NO actualizar sesión (mantener CARGA_DATOS para permitir re-intento)
            // NO eliminar de Redis
            throw new BadRequestAlertException(
                "Venta rechazada: " + ventaResponse.getDescripcion(),
                "venta",
                "ventarechazada"
            );
        }
    }
    // ========== MÉTODOS HELPER PRIVADOS ==========
    /**
     * Busca un asiento en la lista de disponibilidad.
     *
     * @param asientos Lista de asientos del evento
     * @param fila Fila del asiento (1-indexed)
     * @param columna Columna del asiento (1-indexed)
     * @return DTO del asiento encontrado
     */
    private AsientoDisponibilidadDTO buscarAsientoEnLista(List<AsientoDisponibilidadDTO> asientos, int fila, int columna) {
        if (asientos == null || asientos.isEmpty()) {
            throw new BadRequestAlertException("Lista de asientos no disponible", "sesion", "listanull");
        }
        for (AsientoDisponibilidadDTO asiento : asientos) {
            if (asiento.getFila().equals(fila) && asiento.getColumna().equals(columna)) {
                return asiento;
            }
        }
        // Si no se encuentra, lanzar excepción
        throw new BadRequestAlertException(
            "Asiento no encontrado: fila " + fila + ", columna " + columna,
            "sesion",
            "asientonotfound"
        );
    }
    /**
     * Valida que un nombre cumple con los requisitos mínimos.
     *
     * @param nombre Nombre a validar
     * @return true si es válido
     */
    private boolean validarNombreCompleto(String nombre) {
        return nombre != null && nombre.trim().length() >= MIN_LONGITUD_NOMBRE;
    }
    /**
     * Valida que las coordenadas de un asiento están dentro del rango del evento.
     *
     * @param evento Evento
     * @param fila Fila del asiento
     * @param columna Columna del asiento
     */
    private void validarCoordenadasAsiento(Evento evento, int fila, int columna) {
        if (fila < 1 || fila > evento.getFilaAsientos()) {
            throw new BadRequestAlertException(
                "Fila inválida: debe estar entre 1 y " + evento.getFilaAsientos(),
                "sesion",
                "invalidrow"
            );
        }
        if (columna < 1 || columna > evento.getColumnaAsientos()) {
            throw new BadRequestAlertException(
                "Columna inválida: debe estar entre 1 y " + evento.getColumnaAsientos(),
                "sesion",
                "invalidcolumn"
            );
        }
    }
}
