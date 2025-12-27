package com.evento.mobile.data.model.purchase

import kotlinx.serialization.Serializable
/**
 * Request para iniciar una sesión de compra.
 */
@Serializable
data class StartSessionRequest(
    val eventoId: Long
)