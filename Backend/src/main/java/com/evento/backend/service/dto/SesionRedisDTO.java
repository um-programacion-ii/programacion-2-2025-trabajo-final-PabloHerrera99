package com.evento.backend.service.dto;

import com.evento.backend.domain.enumeration.EstadoSesion;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;
/**
 * - Almacenar estado temporal de sesión (TTL: 30 minutos)
 * - Permitir recuperación de sesión entre múltiples clientes
 * - Mantener asientos seleccionados antes de persistir
 */
public class SesionRedisDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("sesionId")
    private Long sesionId; // Referencia al ID de PostgreSQL (puede ser null si aún no se persiste)

    @JsonProperty("eventoId")
    @NotNull
    private Long eventoId;

    @JsonProperty("userId")
    @NotNull
    private String userId; // Username del usuario autenticado

    @JsonProperty("estado")
    @NotNull
    private EstadoSesion estado;

    @JsonProperty("asientosSeleccionados")
    private List<AsientoSimpleDTO> asientosSeleccionados = new ArrayList<>();

    @JsonProperty("nombresAsignados")
    private Map<String, String> nombresAsignados = new HashMap<>();
    // Key: "fila-columna" (ej: "5-10")
    // Value: "Juan Pérez"

    @JsonProperty("ultimaActividad")
    @NotNull
    private Instant ultimaActividad;

    @JsonProperty("expiracion")
    @NotNull
    private Instant expiracion;

    // Constructor vacío
    public SesionRedisDTO() {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        return "SesionRedisDTO{" +
            "sesionId=" + sesionId +
            ", eventoId=" + eventoId +
            ", userId='" + userId + '\'' +
            ", estado=" + estado +
            ", asientosSeleccionados=" + asientosSeleccionados +
            ", nombresAsignados=" + nombresAsignados +
            ", ultimaActividad=" + ultimaActividad +
            ", expiracion=" + expiracion +
            '}';
    }
}
