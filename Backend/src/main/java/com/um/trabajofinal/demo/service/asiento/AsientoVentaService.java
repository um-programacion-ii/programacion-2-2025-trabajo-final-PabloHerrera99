package com.um.trabajofinal.demo.service.asiento;

import com.um.trabajofinal.demo.api.dto.AsientoDto;
import com.um.trabajofinal.demo.api.dto.SeatAvailabilityDto;

import java.util.List;

public interface AsientoVentaService {
    
    /**
     * Reservar asientos temporalmente (15 minutos)
     */
    List<AsientoDto> holdSeats(Long eventoId, List<String> asientos, Long usuarioId);
    
    /**
     * Liberar asientos reservados
     */
    void releaseSeats(Long eventoId, List<String> asientos);
    
    /**
     * Confirmar asientos en una venta (finalizar compra)
     */
    List<AsientoDto> finalizeSeats(Long ventaId, List<String> asientos);
    
    /**
     * Obtener disponibilidad de asientos para un evento
     */
    SeatAvailabilityDto getAvailableSeats(Long eventoId);
    
    /**
     * Verificar si un asiento específico está disponible
     */
    boolean isSeatAvailable(Long eventoId, String fila, String numero);
    
    /**
     * Liberar asientos vencidos (reservas expiradas)
     */
    void releaseExpiredReservations();
    
    /**
     * Obtener asientos de una venta específica
     */
    List<AsientoDto> getAsientosByVenta(Long ventaId);
}