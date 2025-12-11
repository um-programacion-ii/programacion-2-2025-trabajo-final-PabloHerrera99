package com.evento.backend.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
/**
 * DTO de request para iniciar una nueva sesión de compra.
 *
 * Endpoint: POST /api/sesiones/iniciar
 */
public class IniciarSesionRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "El ID del evento es obligatorio")
    @Min(value = 1, message = "El ID del evento debe ser mayor a 0")
    private Long eventoId;

    // Constructor vacío
    public IniciarSesionRequestDTO() {
    }

    // Constructor con parámetros
    public IniciarSesionRequestDTO(Long eventoId) {
        this.eventoId = eventoId;
    }

    // Getters y Setters
    public Long getEventoId() {
        return eventoId;
    }

    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }

    @Override
    public String toString() {
        return "IniciarSesionRequestDTO{" +
            "eventoId=" + eventoId +
            '}';
    }
}
