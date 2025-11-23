package com.um.trabajofinal.demo.persistence.repository;

import com.um.trabajofinal.demo.persistence.domain.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    
    /**
     * Buscar evento por external ID
     */
    Optional<Evento> findByExternalId(String externalId);
    
    /**
     * Buscar todos los eventos activos
     */
    List<Evento> findByActivoTrue();
    
    /**
     * Buscar eventos por categoría
     */
    List<Evento> findByCategoria(String categoria);
}
