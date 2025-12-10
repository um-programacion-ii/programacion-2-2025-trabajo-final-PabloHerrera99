package com.evento.backend.service.dto;

import com.evento.backend.domain.enumeration.EstadoSesion;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
/**
 * DTO de respuesta con el estado actual de una sesión.
 *
 * Usado en:
 * - POST /api/sesiones/iniciar
 * - GET /api/sesiones/actual
 * - PUT /api/sesiones/seleccionar-asientos
 * - PUT /api/sesiones/asignar-nombres
 */
public class SesionEstadoResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long sesionId;
    private Long eventoId;
    private String tituloEvento;
    private EstadoSesion estado;
    private List<AsientoSimpleDTO> asientosSeleccionados;
    private Map<String, String> nombresAsignados;
    private Integer cantidadAsientos;
    private Long tiempoRestanteSegundos;
    private Instant ultimaActividad;
    private Instant expiracion;

    // Constructor vacío
    public SesionEstadoResponseDTO() {
    }

    /**
     * Crea un SesionEstadoResponseDTO desde un SesionRedisDTO
     */
    public static SesionEstadoResponseDTO fromSesionRedis(
        SesionRedisDTO sesionRedis,
        String tituloEvento
    ) {
        SesionEstadoResponseDTO response = new SesionEstadoResponseDTO();

        response.setSesionId(sesionRedis.getSesionId());
        response.setEventoId(sesionRedis.getEventoId());
        response.setTituloEvento(tituloEvento);
        response.setEstado(sesionRedis.getEstado());
        response.setAsientosSeleccionados(sesionRedis.getAsientosSeleccionados());
        response.setNombresAsignados(sesionRedis.getNombresAsignados());
        response.setUltimaActividad(sesionRedis.getUltimaActividad());
        response.setExpiracion(sesionRedis.getExpiracion());

        // Calcular campos derivados
        response.setCantidadAsientos(
            sesionRedis.getAsientosSeleccionados() != null
                ? sesionRedis.getAsientosSeleccionados().size()
                : 0
        );

        response.setTiempoRestanteSegundos(
            Duration.between(Instant.now(), sesionRedis.getExpiracion()).getSeconds()
        );

        return response;
    }

    // Getters y Setters
    public Long getSesionId() {
        return sesionId;
    }

    public void setSesionId(Long sesionId) {
        this.sesionId = sesionId;
    }

    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    public String getTituloEvento() {
        return tituloEvento;
    }

    public void setTituloEvento(String tituloEvento) {
        this.tituloEvento = tituloEvento;
    }

    public EstadoSesion getEstado() {
        return estado;
    }

    public void setEstado(EstadoSesion estado) {
        this.estado = estado;
    }

    public List<AsientoSimpleDTO> getAsientosSeleccionados() {
        return asientosSeleccionados;
    }

    public void setAsientosSeleccionados(List<AsientoSimpleDTO> asientosSeleccionados) {
        this.asientosSeleccionados = asientosSeleccionados;
    }

    public Map<String, String> getNombresAsignados() {
        return nombresAsignados;
    }

    public void setNombresAsignados(Map<String, String> nombresAsignados) {
        this.nombresAsignados = nombresAsignados;
    }

    public Integer getCantidadAsientos() {
        return cantidadAsientos;
    }

    public void setCantidadAsientos(Integer cantidadAsientos) {
        this.cantidadAsientos = cantidadAsientos;
    }

    public Long getTiempoRestanteSegundos() {
        return tiempoRestanteSegundos;
    }

    public void setTiempoRestanteSegundos(Long tiempoRestanteSegundos) {
        this.tiempoRestanteSegundos = tiempoRestanteSegundos;
    }

    public Instant getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(Instant ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    public Instant getExpiracion() {
        return expiracion;
    }

    public void setExpiracion(Instant expiracion) {
        this.expiracion = expiracion;
    }

    @Override
    public String toString() {
        return "SesionEstadoResponseDTO{" +
            "sesionId=" + sesionId +
            ", eventoId=" + eventoId +
            ", tituloEvento='" + tituloEvento + '\'' +
            ", estado=" + estado +
            ", cantidadAsientos=" + cantidadAsientos +
            ", tiempoRestanteSegundos=" + tiempoRestanteSegundos +
            ", expiracion=" + expiracion +
            '}';
    }
}
