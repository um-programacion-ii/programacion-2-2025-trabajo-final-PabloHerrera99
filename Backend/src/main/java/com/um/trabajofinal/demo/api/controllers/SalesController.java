package com.um.trabajofinal.demo.api.controllers;

import com.um.trabajofinal.demo.service.asiento.AsientoVentaService;
import com.um.trabajofinal.demo.service.venta.VentaService;
import com.um.trabajofinal.demo.api.dto.*;
import com.um.trabajofinal.demo.persistence.domain.enums.EstadoVenta;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Validated
public class SalesController {

    private final AsientoVentaService asientoVentaService;
    private final VentaService ventaService;

    public SalesController(AsientoVentaService asientoVentaService, VentaService ventaService) {
        this.asientoVentaService = asientoVentaService;
        this.ventaService = ventaService;
    }

    @PostMapping("/eventos/{eventoId}/hold-seats")
    public ResponseEntity<ApiResponse<List<AsientoDto>>> holdSeats(
            @PathVariable @Min(1) Long eventoId, 
            @Valid @RequestBody HoldSeatsRequest request) {
        
        List<AsientoDto> asientosReservados = asientoVentaService.holdSeats(
                eventoId, request.getAsientos(), request.getUsuarioId());
        
        return ResponseEntity.ok(ApiResponse.success(
                asientosReservados, 
                "Asientos reservados exitosamente por 15 minutos"));
    }

    @PostMapping("/eventos/{eventoId}/finalize-sale")
    public ResponseEntity<ApiResponse<VentaDto>> finalizeSale(
            @PathVariable @Min(1) Long eventoId, 
            @Valid @RequestBody FinalizeSaleRequest request) {
        
        // Calcular precio total basado en asientos y evento
        BigDecimal precioTotal = calcularPrecioTotal(eventoId, request.getAsientos());
        
        // Crear venta
        VentaDto ventaDto = VentaDto.builder()
                .eventoId(eventoId)
                .usuarioId(request.getUsuarioId())
                .montoTotal(precioTotal)
                .estado(EstadoVenta.PENDIENTE)
                .build();
        
        VentaDto ventaCreada = ventaService.createVenta(ventaDto);
        
        // Finalizar asientos
        List<AsientoDto> asientosFinalizados = asientoVentaService.finalizeSeats(
                ventaCreada.getId(), request.getAsientos());
        
        // Actualizar venta con asientos
        ventaCreada.setAsientos(asientosFinalizados);
        ventaCreada.setEstado(EstadoVenta.CONFIRMADA);
        ventaService.updateVentaEstado(ventaCreada.getId(), EstadoVenta.CONFIRMADA);
        
        return ResponseEntity.ok(ApiResponse.success(
                ventaCreada, 
                "Venta finalizada exitosamente"));
    }

    @PostMapping("/ventas/{ventaId}/finalize-seats")
    public ResponseEntity<ApiResponse<List<AsientoDto>>> finalizeSeats(
            @PathVariable @Min(1) Long ventaId, 
            @Valid @RequestBody FinalizeSeatsRequest request) {
        
        List<AsientoDto> asientosFinalizados = asientoVentaService.finalizeSeats(
                ventaId, request.getAsientos());
        
        return ResponseEntity.ok(ApiResponse.success(
                asientosFinalizados, 
                "Asientos confirmados en la venta"));
    }

    @GetMapping("/asientos/{eventoId}/availability")
    public ResponseEntity<ApiResponse<SeatAvailabilityDto>> getSeatAvailability(
            @PathVariable @Min(1) Long eventoId) {
        
        SeatAvailabilityDto availability = asientoVentaService.getAvailableSeats(eventoId);
        
        return ResponseEntity.ok(ApiResponse.success(
                availability, 
                "Disponibilidad de asientos obtenida exitosamente"));
    }

    private BigDecimal calcularPrecioTotal(Long eventoId, List<String> asientos) {
        // Por ahora, precio fijo por asiento, se puede mejorar después
        return BigDecimal.valueOf(asientos.size()).multiply(BigDecimal.valueOf(1000));
    }
}
