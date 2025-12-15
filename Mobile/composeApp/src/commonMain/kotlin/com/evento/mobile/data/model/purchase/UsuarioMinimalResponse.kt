package com.evento.mobile.data.model.purchase

import kotlinx.serialization.Serializable
/**
 * Versión reducida del objeto Usuario.
 *
 * El backend devuelve el usuario anidado en SessionResponse,
 * pero no lo usamos en la app mobile.
 * Este DTO existe solo para permitir la deserialización correcta.
 */
@Serializable
data class UsuarioMinimalResponse(
    val id: Long,
    val login: String? = null
)