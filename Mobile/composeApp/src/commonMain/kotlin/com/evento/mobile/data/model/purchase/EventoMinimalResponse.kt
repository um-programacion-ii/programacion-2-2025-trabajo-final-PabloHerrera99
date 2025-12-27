package com.evento.mobile.data.model.purchase

import kotlinx.serialization.Serializable
/**
 * Versión reducida del objeto Evento.
 *
 * Se usa cuando el backend devuelve el evento anidado en SessionResponse,
 * pero solo necesitamos extraer el ID para usarlo como eventoId.
 *
 * Nota: El EventoResponse completo está en data.model.event
 */
@Serializable
data class EventoMinimalResponse(
    val id: Long,
    val nombre: String? = null
)