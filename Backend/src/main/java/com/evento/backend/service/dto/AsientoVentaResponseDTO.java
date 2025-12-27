package com.evento.backend.service.dto;

import java.io.Serializable;
import java.util.Objects;
/**
 * DTO para representar un asiento en la respuesta de venta.
 * Parte de la respuesta de POST /api/endpoints/v1/realizar-venta
 */
public class AsientoVentaResponseDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Integer fila;
    private Integer columna;
    private String persona;
    private String estado; // "Vendido", "Libre", "Ocupado"

    // Constructores
    public AsientoVentaResponseDTO() {}
    public AsientoVentaResponseDTO(
        Integer fila,
        Integer columna,
        String persona,
        String estado
    ) {
        this.fila = fila;
        this.columna = columna;
        this.persona = persona;
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
    public String getPersona() {
        return persona;
    }
    public void setPersona(String persona) {
        this.persona = persona;
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
        if (!(o instanceof AsientoVentaResponseDTO)) return false;
        AsientoVentaResponseDTO that = (AsientoVentaResponseDTO) o;
        return Objects.equals(fila, that.fila) &&
            Objects.equals(columna, that.columna) &&
            Objects.equals(persona, that.persona) &&
            Objects.equals(estado, that.estado);
    }
    @Override
    public int hashCode() {
        return Objects.hash(fila, columna, persona, estado);
    }
    @Override
    public String toString() {
        return "AsientoVentaResponseDTO{" +
            "fila=" + fila +
            ", columna=" + columna +
            ", persona='" + persona + '\'' +
            ", estado='" + estado + '\'' +
            '}';
    }
}
