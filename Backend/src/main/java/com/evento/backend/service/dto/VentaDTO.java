package com.evento.backend.service.dto;

import com.evento.backend.domain.enumeration.EstadoSincronizacion;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.evento.backend.domain.Venta} entity.
 */
@Schema(
    description = "Entidad 6: Venta\nRegistro de intentos de venta (exitosos y fallidos)\nMantiene estado de sincronización con servidor de cátedra"
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VentaDTO implements Serializable {

    private Long id;

    private Long idVentaCatedra;

    @NotNull
    private Instant fechaVenta;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal precioTotal;

    @NotNull
    private Boolean exitosa;

    @Size(max = 500)
    private String descripcion;

    @NotNull
    private EstadoSincronizacion estadoSincronizacion;

    @NotNull
    private EventoDTO evento;

    @NotNull
    private UserDTO usuario;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdVentaCatedra() {
        return idVentaCatedra;
    }

    public void setIdVentaCatedra(Long idVentaCatedra) {
        this.idVentaCatedra = idVentaCatedra;
    }

    public Instant getFechaVenta() {
        return fechaVenta;
    }

    public void setFechaVenta(Instant fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public BigDecimal getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(BigDecimal precioTotal) {
        this.precioTotal = precioTotal;
    }

    public Boolean getExitosa() {
        return exitosa;
    }

    public void setExitosa(Boolean exitosa) {
        this.exitosa = exitosa;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public EstadoSincronizacion getEstadoSincronizacion() {
        return estadoSincronizacion;
    }

    public void setEstadoSincronizacion(EstadoSincronizacion estadoSincronizacion) {
        this.estadoSincronizacion = estadoSincronizacion;
    }

    public EventoDTO getEvento() {
        return evento;
    }

    public void setEvento(EventoDTO evento) {
        this.evento = evento;
    }

    public UserDTO getUsuario() {
        return usuario;
    }

    public void setUsuario(UserDTO usuario) {
        this.usuario = usuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VentaDTO)) {
            return false;
        }

        VentaDTO ventaDTO = (VentaDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, ventaDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VentaDTO{" +
            "id=" + getId() +
            ", idVentaCatedra=" + getIdVentaCatedra() +
            ", fechaVenta='" + getFechaVenta() + "'" +
            ", precioTotal=" + getPrecioTotal() +
            ", exitosa='" + getExitosa() + "'" +
            ", descripcion='" + getDescripcion() + "'" +
            ", estadoSincronizacion='" + getEstadoSincronizacion() + "'" +
            ", evento=" + getEvento() +
            ", usuario=" + getUsuario() +
            "}";
    }
}
