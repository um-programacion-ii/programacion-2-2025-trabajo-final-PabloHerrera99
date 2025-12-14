package com.evento.mobile.data.model.auth

import kotlinx.serialization.Serializable

/**
 * Request para autenticación de usuario.
 *
 * Este DTO (Data Transfer Object) se envía al endpoint POST /api/authenticate
 * con las credenciales del usuario.
 * @property username Nombre de usuario
 * @property password Contraseña del usuario
 * @property rememberMe Flag para recordar sesión (siempre false en nuestra implementación)
 */

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val rememberMe: Boolean = false
)