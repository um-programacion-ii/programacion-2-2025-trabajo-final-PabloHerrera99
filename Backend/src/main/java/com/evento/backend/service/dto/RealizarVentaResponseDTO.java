package com.evento.backend.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
/**
 * DTO para la respuesta completa de venta desde servidor de cátedra.
 * Response de POST /api/endpoints/v1/realizar-venta
 */
public class RealizarVentaResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long eventoId;
    private Long ventaId; // Puede ser null si la venta falló
    private Instant fechaVenta;
    private List<AsientoVentaResponseDTO> asientos;
    private Boolean resultado;
    private String descripcion;
    private BigDecimal precioVenta;

    // Constructores
    public RealizarVentaResponseDTO() {}
    public RealizarVentaResponseDTO(
        Long eventoId,
        Long ventaId,
        Instant fechaVenta,
        List<AsientoVentaResponseDTO> asientos,
        Boolean resultado,
        String descripcion,
        BigDecimal precioVenta
    ) {
        this.eventoId = eventoId;
        this.ventaId = ventaId;
        this.fechaVenta = fechaVenta;
        this.asientos = asientos;
        this.resultado = resultado;
        this.descripcion = descripcion;
        this.precioVenta = precioVenta;
    }
    // Getters y Setters
    public Long getEventoId() {
        return eventoId;
    }
    public void setEventoId(Long eventoId) {
        this.eventoId = eventoId;
    }
    public Long getVentaId() {
        return ventaId;
    }
    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }
    public Instant getFechaVenta() {
        return fechaVenta;
    }
    public void setFechaVenta(Instant fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
    public List<AsientoVentaResponseDTO> getAsientos() {
        return asientos;
    }
    public void setAsientos(List<AsientoVentaResponseDTO> asientos) {
        this.asientos = asientos;
    }
    public Boolean getResultado() {
        return resultado;
    }
    public void setResultado(Boolean resultado) {
        this.resultado = resultado;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }
    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealizarVentaResponseDTO)) return false;
        RealizarVentaResponseDTO that = (RealizarVentaResponseDTO) o;
        return Objects.equals(eventoId, that.eventoId) &&
            Objects.equals(ventaId, that.ventaId) &&
            Objects.equals(fechaVenta, that.fechaVenta) &&
            Objects.equals(asientos, that.asientos) &&
            Objects.equals(resultado, that.resultado) &&
            Objects.equals(descripcion, that.descripcion) &&
            Objects.equals(precioVenta, that.precioVenta);
    }
    @Override
    public int hashCode() {
        return Objects.hash(eventoId, ventaId, fechaVenta, asientos,
            resultado, descripcion, precioVenta);
    }
    @Override
    public String toString() {
        return "RealizarVentaResponseDTO{" +
            "eventoId=" + eventoId +
            ", ventaId=" + ventaId +
            ", fechaVenta=" + fechaVenta +
            ", asientos=" + asientos +
            ", resultado=" + resultado +
            ", descripcion='" + descripcion + '\'' +
            ", precioVenta=" + precioVenta +
            '}';
    }
}
