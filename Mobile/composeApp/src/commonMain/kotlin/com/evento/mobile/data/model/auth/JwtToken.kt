package com.evento.mobile.data.model.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
/**
 * Response del endpoint de autenticación.
 *
 * Este DTO representa la respuesta que el backend envía cuando
 * la autenticación es exitosa. Contiene el token JWT que se
 * usará para autenticar futuras peticiones.
 *
 * Ejemplo de JSON recibido:
 * ```json
 * {
 *   "id_token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0x..."
 * }
 * *
 * @property idToken Token JWT para autenticación (mapeado desde "id_token" en JSON)
 */
@Serializable
data class JwtToken(
    @SerialName("id_token")
    val idToken: String
)