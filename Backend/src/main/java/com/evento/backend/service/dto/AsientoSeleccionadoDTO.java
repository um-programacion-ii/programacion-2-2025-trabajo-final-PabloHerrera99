package com.evento.backend.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.evento.backend.domain.AsientoSeleccionado} entity.
 */
@Schema(
    description = "Entidad 5: AsientoSeleccionado\nAsientos que el usuario ha seleccionado durante su sesión\nMáximo 4 asientos por sesión (validado en lógica de negocio)"
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AsientoSeleccionadoDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer fila;

    @NotNull
    @Min(value = 1)
    private Integer columna;

    @Size(max = 200)
    private String nombrePersona;

    @NotNull
    private SesionDTO sesion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getNombrePersona() {
        return nombrePersona;
    }

    public void setNombrePersona(String nombrePersona) {
        this.nombrePersona = nombrePersona;
    }

    public SesionDTO getSesion() {
        return sesion;
    }

    public void setSesion(SesionDTO sesion) {
        this.sesion = sesion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AsientoSeleccionadoDTO)) {
            return false;
        }

        AsientoSeleccionadoDTO asientoSeleccionadoDTO = (AsientoSeleccionadoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, asientoSeleccionadoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AsientoSeleccionadoDTO{" +
            "id=" + getId() +
            ", fila=" + getFila() +
            ", columna=" + getColumna() +
            ", nombrePersona='" + getNombrePersona() + "'" +
            ", sesion=" + getSesion() +
            "}";
    }
}
