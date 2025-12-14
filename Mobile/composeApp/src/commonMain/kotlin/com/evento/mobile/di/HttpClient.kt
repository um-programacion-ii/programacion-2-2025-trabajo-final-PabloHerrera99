package com.evento.mobile.di

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Proveedor del cliente HTTP configurado para toda la aplicación.
 *
 * Este objeto se encarga de crear y configurar el HttpClient de Ktor
 * con todos los plugins necesarios para comunicarse con el backend.
 */

object HttpClientProvider {

    /**
     * Función que provee el token JWT actual para autenticación.
     * Se configura desde AppNavigation cuando se inicializa el AuthRepository.
     */
    private var tokenProvider: (() -> String?)? = null

    /**
     * Configura el proveedor de token para autenticación Bearer.
     * @param provider Función que retorna el token JWT actual o null si no hay sesión
     */
    fun setTokenProvider(provider: () -> String?) {
        tokenProvider = provider
    }

    /**
     * Crea y configura el HttpClient con todos los plugins necesarios:
     *
     * - ContentNegotiation: Serialización/deserialización automática JSON
     * - Logging: Logs de peticiones HTTP para debugging
     * - Auth: Autenticación Bearer con JWT
     * - HttpTimeout: Timeouts configurados para conexión y peticiones
     * - DefaultRequest: Configuración base de todas las peticiones
     *
     * @return HttpClient configurado y listo para usar
     */
    fun create(): HttpClient = HttpClient {

        // Plugin de serialización JSON
        install(ContentNegotiation) {
            json(Json {

                // Ignorar campos del JSON que no están en el modelo Kotlin
                ignoreUnknownKeys = true

                // Permitir JSON no estrictamente válido (comillas simples, etc.)
                isLenient = true

                // Formatear el JSON en los logs para mejor legibilidad
                prettyPrint = true

                // Permitir valores null explícitos en JSON
                explicitNulls = false
            })
        }

        // Plugin de logging para debugging
        install(Logging) {

            // LogLevel.INFO muestra:
            // - URL de la petición
            // - Método HTTP (GET, POST, etc.)
            // - Código de respuesta (200, 401, 500, etc.)
            //
            // No muestra el body completo (para eso usar LogLevel.ALL)
            level = LogLevel.INFO

            logger = Logger.DEFAULT
        }

        // Plugin de autenticación Bearer (JWT)
        install(Auth) {
            bearer {

                // Cargar el token actual cuando se necesite
                loadTokens {
                    tokenProvider?.invoke()?.let { token ->
                        // Retornar el token en formato Bearer
                        BearerTokens(accessToken = token, refreshToken = "")
                    }
                }

                // No enviar credenciales sin autenticación previa
                sendWithoutRequest { request ->
                    // Solo enviar token si ya lo tenemos
                    // (evita enviar en /api/authenticate)
                    request.url.encodedPath != Endpoints.AUTHENTICATE
                }
            }
        }

        // Plugin de timeout
        install(HttpTimeout) {
            // Tiempo máximo para completar toda la petición
            requestTimeoutMillis = 30_000  // 30 segundos

            // Tiempo máximo para establecer la conexión
            connectTimeoutMillis = 15_000  // 15 segundos

            // Tiempo máximo entre bytes recibidos
            socketTimeoutMillis = 30_000   // 30 segundos
        }

        // Configuración aplicada a TODAS las peticiones por defecto
        defaultRequest {
            // URL base del backend (viene de ApiConfig)
            url(ApiConfig.BASE_URL)

            // Content-Type por defecto: application/json
            contentType(ContentType.Application.Json)
        }
    }
}