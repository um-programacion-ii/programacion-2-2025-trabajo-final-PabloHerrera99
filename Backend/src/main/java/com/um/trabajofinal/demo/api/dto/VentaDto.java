package com.um.trabajofinal.demo.api.dto;

import com.um.trabajofinal.demo.persistence.domain.enums.EstadoVenta;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaDto {
    private Long id;
    private String externalId;
    private Long eventoId;
    private String eventoNombre;
    private Long usuarioId;
    private String usuarioEmail;
    private BigDecimal montoTotal;
    private String moneda;
    private EstadoVenta estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private List<AsientoDto> asientos;
}