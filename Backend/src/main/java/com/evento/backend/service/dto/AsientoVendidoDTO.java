package com.evento.backend.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.evento.backend.domain.AsientoVendido} entity.
 */
@Schema(description = "Entidad 7: AsientoVendido\nDetalle de asientos incluidos en una venta completada")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AsientoVendidoDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer fila;

    @NotNull
    @Min(value = 1)
    private Integer columna;

    @NotNull
    @Size(max = 200)
    private String nombrePersona;

    @Size(max = 50)
    private String estado;

    @NotNull
    private VentaDTO venta;

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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public VentaDTO getVenta() {
        return venta;
    }

    public void setVenta(VentaDTO venta) {
        this.venta = venta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AsientoVendidoDTO)) {
            return false;
        }

        AsientoVendidoDTO asientoVendidoDTO = (AsientoVendidoDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, asientoVendidoDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AsientoVendidoDTO{" +
            "id=" + getId() +
            ", fila=" + getFila() +
            ", columna=" + getColumna() +
            ", nombrePersona='" + getNombrePersona() + "'" +
            ", estado='" + getEstado() + "'" +
            ", venta=" + getVenta() +
            "}";
    }
}
