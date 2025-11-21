package com.um.trabajofinal.demo.persistence.repository;

import com.um.trabajofinal.demo.persistence.domain.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    Optional<Evento> findByExternalId(String externalId);
}
