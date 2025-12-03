package com.evento.backend.repository;

import com.evento.backend.domain.Evento;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Evento entity.
 */
@Repository
public interface EventoRepository extends JpaRepository<Evento, Long>, JpaSpecificationExecutor<Evento> {
    default Optional<Evento> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Evento> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Evento> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select evento from Evento evento left join fetch evento.eventoTipo",
        countQuery = "select count(evento) from Evento evento"
    )
    Page<Evento> findAllWithToOneRelationships(Pageable pageable);

    @Query("select evento from Evento evento left join fetch evento.eventoTipo")
    List<Evento> findAllWithToOneRelationships();

    @Query("select evento from Evento evento left join fetch evento.eventoTipo where evento.id =:id")
    Optional<Evento> findOneWithToOneRelationships(@Param("id") Long id);
}
