package com.um.trabajofinal.demo.persistence.repository;

import com.um.trabajofinal.demo.persistence.domain.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    
    /**
     * Buscar ventas por usuario
     */
    List<Venta> findByUsuarioId(Long usuarioId);
}
