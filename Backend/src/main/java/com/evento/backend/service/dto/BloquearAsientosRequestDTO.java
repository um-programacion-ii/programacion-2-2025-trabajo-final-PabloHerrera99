package com.evento.backend.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;
/**
 * DTO para solicitar bloqueo de asientos en servidor de cátedra.
 * Request para POST /api/endpoints/v1/bloquear-asientos
 */
public class BloquearAsientosRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "El ID del evento es obligatorio")
    private Long eventoId;

    @NotEmpty(message = "Debe proporcionar al menos un asiento para bloquear")
    @Valid
    private List<AsientoSimpleDTO> asientos;
    // Constructores
    public BloquearAsientosRequestDTO() {}
    public BloquearAsientosRequestDTO(Long eventoId, List<AsientoSimpleDTO> asientos) {
        this.eventoId = eventoId;
        this.asientos = asientos;
    }
    // Getters y Setters
    public Long getEventoId() {
        return eventoId;
    }
    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }
    public List<AsientoSimpleDTO> getAsientos() {
        return asientos;
    }
    public void setAsientos(List<AsientoSimpleDTO> asientos) {
        this.asientos = asientos;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BloquearAsientosRequestDTO)) return false;
        BloquearAsientosRequestDTO that = (BloquearAsientosRequestDTO) o;
        return Objects.equals(eventoId, that.eventoId) &&
            Objects.equals(asientos, that.asientos);
    }
    @Override
    public int hashCode() {
        return Objects.hash(eventoId, asientos);
    }
    @Override
    public String toString() {
        return "BloquearAsientosRequestDTO{" +
            "eventoId=" + eventoId +
            ", asientos=" + asientos +
            '}';
    }
}
