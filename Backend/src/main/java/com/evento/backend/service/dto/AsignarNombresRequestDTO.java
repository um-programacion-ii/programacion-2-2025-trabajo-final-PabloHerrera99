package com.evento.backend.service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.Map;
/**
 * DTO de request para asignar nombres a los asientos seleccionados.
 *
 * Endpoint: PUT /api/sesiones/asignar-nombres
 *
 * Formato del Map:
 * - Key: "fila-columna" (ejemplo: "5-10")
 * - Value: Nombre completo de la persona (ejemplo: "Juan Pérez")
 */
public class AsignarNombresRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "El mapa de nombres es obligatorio")
    @Size(min = 1, message = "Debe asignar al menos un nombre")
    private Map<String, String> nombres;

    // Constructor vacío
    public AsignarNombresRequestDTO() {
    }

    // Constructor con parámetros
    public AsignarNombresRequestDTO(Map<String, String> nombres) {
        this.nombres = nombres;
    }

    // Getters y Setters
    public Map<String, String> getNombres() {
        return nombres;
    }

    public void setNombres(Map<String, String> nombres) {
        this.nombres = nombres;
    }

    @Override
    public String toString() {
        return "AsignarNombresRequestDTO{" +
            "nombres=" + nombres +
            '}';
    }
}
