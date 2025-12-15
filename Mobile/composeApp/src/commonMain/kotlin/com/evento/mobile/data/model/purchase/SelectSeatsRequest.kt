package com.evento.mobile.data.model.purchase

import kotlinx.serialization.Serializable
/**
 * Request para seleccionar (bloquear) asientos en una sesión.
 */
@Serializable
data class SelectSeatsRequest(
    val asientos: List<SeatCoordinates>
)