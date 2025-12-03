package com.evento.backend.repository;

import com.evento.backend.domain.Integrante;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Integrante entity.
 */
@Repository
public interface IntegranteRepository extends JpaRepository<Integrante, Long> {
    default Optional<Integrante> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Integrante> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Integrante> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select integrante from Integrante integrante left join fetch integrante.evento",
        countQuery = "select count(integrante) from Integrante integrante"
    )
    Page<Integrante> findAllWithToOneRelationships(Pageable pageable);

    @Query("select integrante from Integrante integrante left join fetch integrante.evento")
    List<Integrante> findAllWithToOneRelationships();

    @Query("select integrante from Integrante integrante left join fetch integrante.evento where integrante.id =:id")
    Optional<Integrante> findOneWithToOneRelationships(@Param("id") Long id);
}
