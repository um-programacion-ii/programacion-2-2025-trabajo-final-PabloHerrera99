package com.evento.backend.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
/**
 * DTO de request para seleccionar asientos en una sesión.
 * Endpoint: PUT /api/sesiones/seleccionar-asientos
 * Validación: Mínimo 1 asiento, máximo 4 asientos por sesión.
 */
public class SeleccionarAsientosRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull(message = "La lista de asientos es obligatoria")
    @Size(min = 1, max = 4, message = "Debe seleccionar entre 1 y 4 asientos")
    @Valid
    private List<AsientoSimpleDTO> asientos;

    // Constructor vacío
    public SeleccionarAsientosRequestDTO() {
    }

    // Constructor con parámetros
    public SeleccionarAsientosRequestDTO(List<AsientoSimpleDTO> asientos) {
        this.asientos = asientos;
    }

    // Getters y Setters
    public List<AsientoSimpleDTO> getAsientos() {
        return asientos;
    }

    public void setAsientos(List<AsientoSimpleDTO> asientos) {
        this.asientos = asientos;
    }

    @Override
    public String toString() {
        return "SeleccionarAsientosRequestDTO{" +
            "asientos=" + asientos +
            '}';
    }
}
