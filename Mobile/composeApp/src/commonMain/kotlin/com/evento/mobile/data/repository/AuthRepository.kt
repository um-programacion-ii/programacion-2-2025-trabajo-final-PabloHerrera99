package com.evento.mobile.data.repository

import com.evento.mobile.data.model.auth.JwtToken
import com.evento.mobile.data.remote.AuthApiService
import com.evento.mobile.util.NetworkResult

/**
 * Repository para operaciones de autenticación.
 *
 * Este repository implementa el patrón Repository, actuando como
 * intermediario entre la capa de presentación (ViewModels) y la
 * capa de datos (API Services).
 *
 * Responsabilidades:
 * - Coordinar llamadas al AuthApiService
 * - Mantener el estado del token JWT en memoria
 * - Proveer métodos para verificar autenticación
 * - Gestionar logout
 */
class AuthRepository(private val authApiService: AuthApiService) {

    /**
     * Token JWT almacenado en memoria.
     */
    private var currentToken: String? = null

    /**
     * Autentica un usuario y guarda el token si es exitoso.
     *
     * Este método:
     * 1. Llama al AuthApiService para hacer login
     * 2. Si es exitoso, guarda el token en memoria
     * 3. Retorna el resultado (Success o Error)
     *
     * @param username Nombre de usuario
     * @param password Contraseña del usuario
     * @return NetworkResult.Success con el token JWT o NetworkResult.Error
     */
    suspend fun login(username: String, password: String): NetworkResult<JwtToken> {

        // Llamar al servicio de API
        val result = authApiService.login(username, password)

        // Si el login fue exitoso, guardar el token
        if (result is NetworkResult.Success) {
            currentToken = result.data.idToken
        }

        return result
    }

    /**
     * Obtiene el token JWT actual.
     *
     * Este método es usado por HttpClientProvider para agregar
     * el header Authorization: Bearer <token> a las peticiones.
     *
     * @return Token JWT o null si no hay sesión activa
     */
    fun getToken(): String? = currentToken

    /**
     * Verifica si hay un usuario autenticado.
     * @return true si existe un token (usuario autenticado), false en caso contrario
     */
    fun isAuthenticated(): Boolean = currentToken != null

    /**
     * Cierra la sesión del usuario y elimina el token.
     *
     * Este método:
     * 1. Limpia el token de memoria
     * 2. El HttpClient automáticamente dejará de enviar el header Authorization
     */
    fun logout() {
        currentToken = null
    }
}