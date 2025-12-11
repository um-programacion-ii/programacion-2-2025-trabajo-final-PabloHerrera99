package com.evento.backend.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
/**
 * DTO para representar un asiento con nombre de persona para venta en servidor de cátedra.
 * Usado en el request de POST /api/endpoints/v1/realizar-venta
 */
public class AsientoVentaDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "La fila es obligatoria")
    @Min(value = 1, message = "La fila debe ser mayor o igual a 1")
    private Integer fila;

    @NotNull(message = "La columna es obligatoria")
    @Min(value = 1, message = "La columna debe ser mayor o igual a 1")
    private Integer columna;

    @NotBlank(message = "El nombre de la persona es obligatorio")
    @Size(max = 200, message = "El nombre no puede exceder 200 caracteres")
    private String persona;

    // Constructores
    public AsientoVentaDTO() {}
    public AsientoVentaDTO(Integer fila, Integer columna, String persona) {
        this.fila = fila;
        this.columna = columna;
        this.persona = persona;
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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AsientoVentaDTO)) return false;
        AsientoVentaDTO that = (AsientoVentaDTO) o;
        return Objects.equals(fila, that.fila) &&
            Objects.equals(columna, that.columna) &&
            Objects.equals(persona, that.persona);
    }
    @Override
    public int hashCode() {
        return Objects.hash(fila, columna, persona);
    }
    @Override
    public String toString() {
        return "AsientoVentaDTO{" +
            "fila=" + fila +
            ", columna=" + columna +
            ", persona='" + persona + '\'' +
            '}';
    }
}
