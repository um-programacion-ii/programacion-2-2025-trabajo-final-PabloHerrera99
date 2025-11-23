package com.um.trabajofinal.demo.api.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoResumeDto {
    private Long id;
    private String nombre;
    private LocalDateTime fechaHora;
    private BigDecimal precioBase;
    private boolean activo;
}
