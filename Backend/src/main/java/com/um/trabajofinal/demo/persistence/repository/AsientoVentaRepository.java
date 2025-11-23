package com.um.trabajofinal.demo.persistence.repository;

import com.um.trabajofinal.demo.persistence.domain.AsientoVenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsientoVentaRepository extends JpaRepository<AsientoVenta, Long> {
    
    /**
     * Buscar asientos por evento
     */
    List<AsientoVenta> findByVentaEventoId(Long eventoId);
    
    /**
     * Verificar si un asiento específico existe en un evento
     */
    boolean existsByVentaEventoIdAndFilaAndColumna(Long eventoId, String fila, String columna);
    
    /**
     * Buscar asientos por venta
     */
    List<AsientoVenta> findByVentaId(Long ventaId);
}
