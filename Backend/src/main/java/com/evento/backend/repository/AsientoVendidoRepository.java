package com.evento.backend.repository;

import com.evento.backend.domain.AsientoVendido;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AsientoVendido entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AsientoVendidoRepository extends JpaRepository<AsientoVendido, Long> {}
