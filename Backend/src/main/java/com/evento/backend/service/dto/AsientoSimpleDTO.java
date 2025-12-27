package com.evento.backend.service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;
/**
 * DTO para representar coordenadas de un asiento.
 * Usado en operaciones de selección de asientos donde no se necesita
 * el ID de base de datos.
 */
public class AsientoSimpleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "La fila es obligatoria")
    @Min(value = 1, message = "La fila debe ser mayor o igual a 1")
    private Integer fila;

    @NotNull(message = "La columna es obligatoria")
    @Min(value = 1, message = "La columna debe ser mayor o igual a 1")
    private Integer columna;

    // Constructor vacío
    public AsientoSimpleDTO() {
    }

    // Constructor con parámetros
    public AsientoSimpleDTO(Integer fila, Integer columna) {
        this.fila = fila;
        this.columna = columna;
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

    /**
     * Genera la key en formato "fila-columna" para usar en mapas
     */
    public String toKey() {
        return fila + "-" + columna;
    }

    /**
     * Crea un AsientoSimpleDTO desde una key en formato "fila-columna"
     */
    public static AsientoSimpleDTO fromKey(String key) {
        String[] parts = key.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Formato de key inválido: " + key);
        }
        return new AsientoSimpleDTO(
            Integer.parseInt(parts[0]),
            Integer.parseInt(parts[1])
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AsientoSimpleDTO)) return false;
        AsientoSimpleDTO that = (AsientoSimpleDTO) o;
        return Objects.equals(fila, that.fila) &&
            Objects.equals(columna, that.columna);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fila, columna);
    }

    @Override
    public String toString() {
        return "AsientoSimpleDTO{" +
            "fila=" + fila +
            ", columna=" + columna +
            '}';
    }
}
