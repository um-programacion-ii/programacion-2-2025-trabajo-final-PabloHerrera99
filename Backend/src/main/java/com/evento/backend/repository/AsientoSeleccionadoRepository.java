package com.evento.backend.repository;

import com.evento.backend.domain.AsientoSeleccionado;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the AsientoSeleccionado entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AsientoSeleccionadoRepository extends JpaRepository<AsientoSeleccionado, Long> {}
