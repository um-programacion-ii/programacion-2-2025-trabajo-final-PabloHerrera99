package com.evento.mobile.data.model.seat

import com.evento.mobile.util.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

/**
 * DTO que representa la disponibilidad de un asiento individual.
 *
 * Mapea desde: AsientoDisponibilidadDTO.java del backend
 */
@Serializable
data class AsientoDisponibilidadResponse(
    /**
     * Número de fila (1-based).
     * Ejemplo: 1, 2, 3, ... 20
     */
    val fila: Int,

    /**
     * Número de columna (1-based).
     * Ejemplo: 1, 2, 3, ... 10
     */
    val columna: Int,

    /**
     * Estado actual del asiento.
     */
    val estado: EstadoAsiento,

    /**
     * Timestamp de expiración del bloqueo.
     * Solo presente cuando estado = BLOQUEADO.
     */
    @Serializable(with = InstantSerializer::class)
    val expira: Instant? = null,

    /**
     * Nombre de la persona asociada al asiento.
     * Solo presente cuando estado = VENDIDO o BLOQUEADO.
     */
    val nombrePersona: String? = null
)