package com.um.trabajofinal.demo.repository;

import com.um.trabajofinal.demo.domain.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
}
