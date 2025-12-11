package com.evento.backend.service.dto;

import java.io.Serializable;
import java.util.Objects;
/**
 * DTO para representar el estado de un asiento en la respuesta de bloqueo.
 * Parte de la respuesta de POST /api/endpoints/v1/bloquear-asientos
 */
public class AsientoBloqueoResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer fila;
    private Integer columna;
    private String estado; // "Bloqueo exitoso", "Ocupado", "Bloqueado"

    // Constructores
    public AsientoBloqueoResponseDTO() {}
    public AsientoBloqueoResponseDTO(Integer fila, Integer columna, String estado) {
        this.fila = fila;
        this.columna = columna;
        this.estado = estado;
    }
    // Getters y Setters
    public Integer getFila() {
        return fila;
    }
    public void setFila(Integer fila) {
        this.fila = fila;
    }
    public Integer getColumna() {
        return columna;
    }
    public void setColumna(Integer columna) {
        this.columna = columna;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AsientoBloqueoResponseDTO)) return false;
        AsientoBloqueoResponseDTO that = (AsientoBloqueoResponseDTO) o;
        return Objects.equals(fila, that.fila) &&
            Objects.equals(columna, that.columna) &&
            Objects.equals(estado, that.estado);
    }
    @Override
    public int hashCode() {
        return Objects.hash(fila, columna, estado);
    }
    @Override
    public String toString() {
        return "AsientoBloqueoResponseDTO{" +
            "fila=" + fila +
            ", columna=" + columna +
            ", estado='" + estado + '\'' +
            '}';
    }
}
