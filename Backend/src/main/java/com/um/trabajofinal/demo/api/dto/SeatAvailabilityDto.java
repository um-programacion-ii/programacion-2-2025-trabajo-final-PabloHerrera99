package com.um.trabajofinal.demo.api.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatAvailabilityDto {
    private Long eventoId;
    private String eventoNombre;
    private int totalAsientos;
    private int asientosDisponibles;
    private int asientosOcupados;
    private int asientosReservados;
    private List<AsientoDto> asientos;

    // Métodos de utilidad
    public double getPorcentajeOcupacion() {
        if (totalAsientos == 0) return 0.0;
        return ((double) asientosOcupados / totalAsientos) * 100;
    }

    public boolean hayDisponibilidad() {
        return asientosDisponibles > 0;
    }
}