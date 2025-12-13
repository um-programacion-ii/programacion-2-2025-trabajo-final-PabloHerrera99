package com.evento.mobile.data.remote

import com.evento.mobile.data.model.auth.JwtToken
import com.evento.mobile.data.model.auth.LoginRequest
import com.evento.mobile.di.Endpoints
import com.evento.mobile.util.NetworkResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

/**
 * Servicio de API para operaciones de autenticación.
 *
 * Esta clase encapsula todas las llamadas HTTP relacionadas con
 * autenticación (login, logout, refresh token, etc.)
 */
class AuthApiService(private val httpClient: HttpClient) {

    /**
     * Autentica un usuario con username y password.
     *
     * Realiza una petición POST a /api/authenticate con las credenciales
     * del usuario y retorna el token JWT si la autenticación es exitosa.
     *
     * Manejo de errores:
     * - 200 OK: Autenticación exitosa, retorna token
     * - 401 Unauthorized: Credenciales incorrectas
     * - 500 Internal Server Error: Error del servidor
     * - Timeout: El servidor tardó demasiado en responder
     * - Connection Error: No hay conexión con el servidor
     *
     * @param username Nombre de usuario
     * @param password Contraseña del usuario
     * @return NetworkResult.Success con el token JWT o NetworkResult.Error con mensaje descriptivo
     */
    suspend fun login(username: String, password: String): NetworkResult<JwtToken> {
        return try {

            // Realizar petición POST con las credenciales
            val response = httpClient.post(Endpoints.AUTHENTICATE) {
                // Ktor serializa automáticamente el LoginRequest a JSON
                setBody(LoginRequest(
                    username = username,
                    password = password,
                    rememberMe = false
                ))
            }

            // Manejar la respuesta según el código HTTP
            when (response.status) {
                HttpStatusCode.OK -> {
                    // Deserializar el JSON a JwtToken automáticamente
                    val jwtToken = response.body<JwtToken>()
                    NetworkResult.Success(jwtToken)
                }

                HttpStatusCode.Unauthorized -> {
                    // 401: Usuario o contraseña incorrectos
                    NetworkResult.Error(
                        message = "Usuario o contraseña incorrectos",
                        code = 401
                    )
                }

                HttpStatusCode.InternalServerError -> {
                    // 500: Error del servidor
                    NetworkResult.Error(
                        message = "Error del servidor. Intenta más tarde",
                        code = 500
                    )
                }

                else -> {
                    // Otros códigos HTTP no esperados
                    NetworkResult.Error(
                        message = "Error desconocido: ${response.status.description}",
                        code = response.status.value
                    )
                }
            }
        } catch (e: Exception) {
            // Manejar errores de red (timeout, conexión, etc.)
            val errorMessage = when {
                // Error de resolución de host (backend no alcanzable)
                e.message?.contains("Unable to resolve host", ignoreCase = true) == true ||
                        e.message?.contains("Failed to connect", ignoreCase = true) == true -> {
                    "No hay conexión con el servidor"
                }

                // Timeout (servidor muy lento o sin respuesta)
                e.message?.contains("timeout", ignoreCase = true) == true -> {
                    "El servidor tardó demasiado en responder"
                }

                // Otros errores de conexión
                else -> {
                    "Error de conexión: ${e.message ?: "Desconocido"}"
                }
            }

            NetworkResult.Error(message = errorMessage)
        }
    }
}