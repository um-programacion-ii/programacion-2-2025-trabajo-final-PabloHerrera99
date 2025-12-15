package com.evento.mobile.data.model.seat

import kotlinx.serialization.Serializable
/**
 * Estados posibles de un asiento.
 *
 * Mapea desde: EstadoAsiento.java (enum) del backend
 */
@Serializable
enum class EstadoAsiento {
    /**
     * Asiento disponible para comprar.
     */
    DISPONIBLE,

    /**
     * Asiento temporalmente bloqueado por otro usuario.
     * El bloqueo expira en 5 minutos.
     */
    BLOQUEADO,

    /**
     * Asiento ya vendido (compra confirmada).
     */
    VENDIDO
}