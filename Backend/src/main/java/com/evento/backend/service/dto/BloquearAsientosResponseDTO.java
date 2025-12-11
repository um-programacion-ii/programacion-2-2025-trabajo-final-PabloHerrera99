package com.evento.backend.service.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
/**
 * DTO para la respuesta completa de bloqueo de asientos desde servidor de cátedra.
 * Response de POST /api/endpoints/v1/bloquear-asientos
 */
public class BloquearAsientosResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Boolean resultado;
    private String descripcion;
    private Long eventoId;
    private List<AsientoBloqueoResponseDTO> asientos;

    // Constructores
    public BloquearAsientosResponseDTO() {}
    public BloquearAsientosResponseDTO(
        Boolean resultado,
        String descripcion,
        Long eventoId,
        List<AsientoBloqueoResponseDTO> asientos
    ) {
        this.resultado = resultado;
        this.descripcion = descripcion;
        this.eventoId = eventoId;
        this.asientos = asientos;
    }
    // Getters y Setters
    public Boolean getResultado() {
        return resultado;
    }
    public void setResultado(Boolean resultado) {
        this.resultado = resultado;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public Long getEventoId() {
        return eventoId;
    }
    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }
    public List<AsientoBloqueoResponseDTO> getAsientos() {
        return asientos;
    }
    public void setAsientos(List<AsientoBloqueoResponseDTO> asientos) {
        this.asientos = asientos;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BloquearAsientosResponseDTO)) return false;
        BloquearAsientosResponseDTO that = (BloquearAsientosResponseDTO) o;
        return Objects.equals(resultado, that.resultado) &&
            Objects.equals(descripcion, that.descripcion) &&
            Objects.equals(eventoId, that.eventoId) &&
            Objects.equals(asientos, that.asientos);
    }
    @Override
    public int hashCode() {
        return Objects.hash(resultado, descripcion, eventoId, asientos);
    }
    @Override
    public String toString() {
        return "BloquearAsientosResponseDTO{" +
            "resultado=" + resultado +
            ", descripcion='" + descripcion + '\'' +
            ", eventoId=" + eventoId +
            ", asientos=" + asientos +
            '}';
    }
}
