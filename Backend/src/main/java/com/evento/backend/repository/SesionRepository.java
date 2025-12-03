package com.evento.backend.repository;

import com.evento.backend.domain.Sesion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Sesion entity.
 */
@Repository
public interface SesionRepository extends JpaRepository<Sesion, Long>, JpaSpecificationExecutor<Sesion> {
    @Query("select sesion from Sesion sesion where sesion.usuario.login = ?#{authentication.name}")
    List<Sesion> findByUsuarioIsCurrentUser();

    default Optional<Sesion> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Sesion> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Sesion> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select sesion from Sesion sesion left join fetch sesion.usuario left join fetch sesion.evento",
        countQuery = "select count(sesion) from Sesion sesion"
    )
    Page<Sesion> findAllWithToOneRelationships(Pageable pageable);

    @Query("select sesion from Sesion sesion left join fetch sesion.usuario left join fetch sesion.evento")
    List<Sesion> findAllWithToOneRelationships();

    @Query("select sesion from Sesion sesion left join fetch sesion.usuario left join fetch sesion.evento where sesion.id =:id")
    Optional<Sesion> findOneWithToOneRelationships(@Param("id") Long id);
}
