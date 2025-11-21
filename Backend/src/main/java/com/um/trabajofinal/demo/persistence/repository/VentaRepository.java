package com.um.trabajofinal.demo.persistence.repository;

import com.um.trabajofinal.demo.persistence.domain.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VentaRepository extends JpaRepository<Venta, Long> {
}
