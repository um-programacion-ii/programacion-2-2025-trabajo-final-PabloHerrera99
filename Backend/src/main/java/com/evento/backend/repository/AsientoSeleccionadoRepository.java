package com.evento.backend.repository;

import com.evento.backend.domain.AsientoSeleccionado;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import com.evento.backend.domain.AsientoSeleccionado;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
/**
 * Spring Data JPA repository for the AsientoSeleccionado entity.
 */
@Repository
public interface AsientoSeleccionadoRepository extends JpaRepository<AsientoSeleccionado, Long> {

    /**
     * Busca todos los asientos seleccionados de una sesión específica.
     *
     * @param sesionId ID de la sesión
     * @return Lista de asientos seleccionados
     */
    @Query("SELECT a FROM AsientoSeleccionado a WHERE a.sesion.id = :sesionId")
    List<AsientoSeleccionado> findBySesionId(@Param("sesionId") Long sesionId);
    /**
     * Elimina todos los asientos seleccionados de una sesión.
     * Útil para re-selección de asientos.
     *
     * @param sesionId ID de la sesión
     */
    @Modifying
    @Query("DELETE FROM AsientoSeleccionado a WHERE a.sesion.id = :sesionId")
    void deleteBySesionId(@Param("sesionId") Long sesionId);
    /**
     * Cuenta la cantidad de asientos seleccionados en una sesión.
     *
     * @param sesionId ID de la sesión
     * @return Cantidad de asientos
     */
    @Query("SELECT COUNT(a) FROM AsientoSeleccionado a WHERE a.sesion.id = :sesionId")
    long countBySesionId(@Param("sesionId") Long sesionId);
}
