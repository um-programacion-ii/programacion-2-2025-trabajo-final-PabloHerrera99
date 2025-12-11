package com.evento.backend.service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
/**
 * DTO para solicitar venta de asientos en servidor de cátedra.
 * Request para POST /api/endpoints/v1/realizar-venta
 */
public class RealizarVentaRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "El ID del evento es obligatorio")
    private Long eventoId;

    @NotNull(message = "La fecha de venta es obligatoria")
    private Instant fecha;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", message = "El precio debe ser mayor o igual a 0")
    private BigDecimal precioVenta;

    @NotEmpty(message = "Debe proporcionar al menos un asiento para vender")
    @Valid
    private List<AsientoVentaDTO> asientos;

    // Constructores
    public RealizarVentaRequestDTO() {}
    public RealizarVentaRequestDTO(
        Long eventoId,
        Instant fecha,
        BigDecimal precioVenta,
        List<AsientoVentaDTO> asientos
    ) {
        this.eventoId = eventoId;
        this.fecha = fecha;
        this.precioVenta = precioVenta;
        this.asientos = asientos;
    }
    // Getters y Setters
    public Long getEventoId() {
        return eventoId;
    }
    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }
    public Instant getFecha() {
        return fecha;
    }
    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }
    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }
    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }
    public List<AsientoVentaDTO> getAsientos() {
        return asientos;
    }
    public void setAsientos(List<AsientoVentaDTO> asientos) {
        this.asientos = asientos;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealizarVentaRequestDTO)) return false;
        RealizarVentaRequestDTO that = (RealizarVentaRequestDTO) o;
        return Objects.equals(eventoId, that.eventoId) &&
            Objects.equals(fecha, that.fecha) &&
            Objects.equals(precioVenta, that.precioVenta) &&
            Objects.equals(asientos, that.asientos);
    }
    @Override
    public int hashCode() {
        return Objects.hash(eventoId, fecha, precioVenta, asientos);
    }
    @Override
    public String toString() {
        return "RealizarVentaRequestDTO{" +
            "eventoId=" + eventoId +
            ", fecha=" + fecha +
            ", precioVenta=" + precioVenta +
            ", asientos=" + asientos +
            '}';
    }
}
