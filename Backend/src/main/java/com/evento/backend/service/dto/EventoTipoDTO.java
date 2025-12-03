package com.evento.backend.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.evento.backend.domain.EventoTipo} entity.
 */
@Schema(description = "Entidad 1: EventoTipo\nTabla de catálogo con tipos de eventos (Conferencia, Teatro, Curso, etc.)")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EventoTipoDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String nombre;

    @Size(max = 200)
    private String descripcion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventoTipoDTO)) {
            return false;
        }

        EventoTipoDTO eventoTipoDTO = (EventoTipoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, eventoTipoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EventoTipoDTO{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            "}";
    }
}
