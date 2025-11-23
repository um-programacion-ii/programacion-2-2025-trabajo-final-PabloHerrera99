package com.um.trabajofinal.demo.api.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsientoDto {
    private Long id;
    private String fila;
    private String numero;
    private BigDecimal precio;
    private String moneda;
    private boolean ocupado;
    private boolean reservadoTemporalmente;
    private LocalDateTime tiempoExpiracionReserva;
    private Long ventaId;

    // Métodos de utilidad
    public String getAsientoCompleto() {
        return fila + numero;
    }

    public boolean isDisponible() {
        return !ocupado && (!reservadoTemporalmente || 
               (tiempoExpiracionReserva != null && tiempoExpiracionReserva.isBefore(LocalDateTime.now())));
    }
}