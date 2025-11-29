package com.um.trabajofinal.demo.api.dto;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Min;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinalizeSeatsRequest {
    @NotNull(message = "El ID de la venta es obligatorio")
    @Min(value = 1, message = "El ID de la venta debe ser mayor a 0")
    private Long ventaId;
    
    @NotNull(message = "La lista de asientos es obligatoria")
    @NotEmpty(message = "Debe especificar al menos un asiento")
    private List<String> asientos;
}
