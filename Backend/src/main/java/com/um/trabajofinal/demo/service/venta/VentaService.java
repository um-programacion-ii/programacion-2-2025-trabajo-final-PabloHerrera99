package com.um.trabajofinal.demo.service.venta;

import com.um.trabajofinal.demo.api.dto.VentaDto;
import com.um.trabajofinal.demo.persistence.domain.enums.EstadoVenta;

import java.util.List;
import java.util.Optional;

public interface VentaService {
    
    /**
     * Crear una nueva venta
     */
    VentaDto createVenta(VentaDto ventaDto);
    
    /**
     * Buscar venta por ID
     */
    Optional<VentaDto> findVentaById(Long id);
    
    /**
     * Buscar ventas por usuario
     */
    List<VentaDto> findVentasByUsuario(Long usuarioId);
    
    /**
     * Actualizar estado de una venta
     */
    VentaDto updateVentaEstado(Long id, EstadoVenta estado);
    
    /**
     * Buscar venta por external ID
     */
    Optional<VentaDto> findVentaByExternalId(String externalId);
    
    /**
     * Obtener todas las ventas (para admin)
     */
    List<VentaDto> findAllVentas();
}