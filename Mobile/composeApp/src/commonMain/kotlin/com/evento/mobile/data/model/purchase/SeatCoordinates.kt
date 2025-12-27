package com.evento.mobile.data.model.purchase

import kotlinx.serialization.Serializable
/**
 * Coordenadas de un asiento (fila y columna).
 *
 * Usado en requests de selección de asientos.
 */
@Serializable
data class SeatCoordinates(
    val fila: Int,
    val columna: Int
)