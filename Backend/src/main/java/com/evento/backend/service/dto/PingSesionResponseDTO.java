package com.evento.backend.service.dto;

import java.io.Serializable;
import java.time.Instant;
/**
 * DTO de respuesta para mantener activa una sesión (keep-alive).
 * Endpoint: POST /api/sesiones/ping
 * REinicia el tiempo de expiración tras extender el TTL.
 */
public class PingSesionResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long tiempoRestanteSegundos;
    private Instant nuevaExpiracion;
    private Boolean sesionActiva;

    // Constructor vacío
    public PingSesionResponseDTO() {
    }

    // Constructor con parámetros
    public PingSesionResponseDTO(Long tiempoRestanteSegundos, Instant nuevaExpiracion, Boolean sesionActiva) {
        this.tiempoRestanteSegundos = tiempoRestanteSegundos;
        this.nuevaExpiracion = nuevaExpiracion;
        this.sesionActiva = sesionActiva;
    }

    // Getters y Setters
    public Long getTiempoRestanteSegundos() {
        return tiempoRestanteSegundos;
    }

    public void setTiempoRestanteSegundos(Long tiempoRestanteSegundos) {
        this.tiempoRestanteSegundos = tiempoRestanteSegundos;
    }

    public Instant getNuevaExpiracion() {
        return nuevaExpiracion;
    }

    public void setNuevaExpiracion(Instant nuevaExpiracion) {
        this.nuevaExpiracion = nuevaExpiracion;
    }

    public Boolean getSesionActiva() {
        return sesionActiva;
    }

    public void setSesionActiva(Boolean sesionActiva) {
        this.sesionActiva = sesionActiva;
    }

    @Override
    public String toString() {
        return "PingSesionResponseDTO{" +
            "tiempoRestanteSegundos=" + tiempoRestanteSegundos +
            ", nuevaExpiracion=" + nuevaExpiracion +
            ", sesionActiva=" + sesionActiva +
            '}';
    }
}
