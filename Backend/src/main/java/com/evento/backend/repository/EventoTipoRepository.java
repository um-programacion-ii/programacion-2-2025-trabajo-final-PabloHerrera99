package com.evento.backend.repository;

import com.evento.backend.domain.EventoTipo;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the EventoTipo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EventoTipoRepository extends JpaRepository<EventoTipo, Long> {

    Optional<EventoTipo> findByNombre(String nombre);
}
