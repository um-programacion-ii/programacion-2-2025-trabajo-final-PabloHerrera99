package com.evento.mobile.data.model.seat

import com.evento.mobile.util.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
/**
 * DTO que representa la matriz completa de asientos de un evento
 * con información de disponibilidad en tiempo real.
 *
 * Mapea desde: MatrizAsientosDTO.java del backend
 */
@Serializable
data class MatrizAsientosResponse(
    /**
     * ID local del evento en la BD del backend.
     */
    val eventoId: Long,

    /**
     * ID del evento en el servidor de cátedra.
     */
    val eventoIdCatedra: Long? = null,

    /**
     * Título del evento.
     */
    val tituloEvento: String,

    /**
     * Cantidad total de filas.
     */
    val totalFilas: Int,

    /**
     * Cantidad total de columnas.
     */
    val totalColumnas: Int,

    /**
     * Cantidad total de asientos (filas × columnas).
     */
    val totalAsientos: Int,

    /**
     * Cantidad de asientos disponibles actualmente.
     */
    val disponibles: Int,

    /**
     * Cantidad de asientos bloqueados (no expirados).
     */
    val bloqueados: Int,

    /**
     * Cantidad de asientos vendidos.
     */
    val vendidos: Int,

    /**
     * Lista completa de asientos con su disponibilidad.
     */
    val asientos: List<AsientoDisponibilidadResponse>,

    /**
     * Timestamp de cuando se consultó esta información.
     */
    @Serializable(with = InstantSerializer::class)
    val consultadoEn: Instant
)