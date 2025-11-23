package com.um.trabajofinal.demo.api.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoDetailDto {
    private Long id;
    private String externalId;
    private String nombre;
    private String descripcion;
    private String categoria;
    private LocalDateTime fechaHora;
    private String lugar;
    private String sala;
    private BigDecimal precioBase;
    private String moneda;
    private boolean activo;
    private int totalAsientos;
    private int asientosDisponibles;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

    // Métodos de utilidad
    public double getPorcentajeDisponibilidad() {
        if (totalAsientos == 0) return 0.0;
        return ((double) asientosDisponibles / totalAsientos) * 100;
    }

    public boolean isVentaActiva() {
        return activo && fechaHora.isAfter(LocalDateTime.now());
    }
}