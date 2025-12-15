package com.evento.mobile.data.model.event

import kotlinx.serialization.Serializable

/**
 * DTO que representa el tipo de un evento.
 *
 * Ejemplos: Conferencia, Teatro, Curso, Música, Deporte
 *
 * Mapea desde: EventoTipoDTO.java del backend
 */

@Serializable
data class EventoTipoResponse(
    val id: Long,
    val nombre: String,
    val descripcion: String? = null
)