package com.evento.mobile.data.remote

import com.evento.mobile.data.model.purchase.*
import com.evento.mobile.di.ApiConfig
import com.evento.mobile.di.Endpoints
import com.evento.mobile.util.NetworkResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
/**
 * Servicio de API para operaciones del proceso de compra.
 *
 * Encapsula todas las llamadas HTTP al backend para:
 * - Iniciar sesión de compra
 * - Seleccionar/bloquear asientos
 * - Asignar nombres a asientos
 * - Confirmar compra
 * - Gestionar estado de sesión
 */
class PurchaseApiService(private val httpClient: HttpClient) {

    /**
     * Inicia una nueva sesión de compra para un evento.
     *
     * @param eventoId ID del evento
     * @return NetworkResult con la sesión creada o error
     */
    suspend fun startSession(eventoId: Long): NetworkResult<SessionResponse> {
        return try {
            val response = httpClient.post(Endpoints.COMPRA_INICIAR) {
                contentType(ContentType.Application.Json)
                setBody(StartSessionRequest(eventoId))
            }

            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.Created -> {
                    NetworkResult.Success(response.body<SessionResponse>())
                }
                HttpStatusCode.BadRequest -> {
                    NetworkResult.Error("Evento no válido o sin asientos", 400)
                }
                HttpStatusCode.Unauthorized -> {
                    NetworkResult.Error("Sesión expirada", 401)
                }
                else -> {
                    NetworkResult.Error(
                        "Error al iniciar sesión: ${response.status.description}",
                        response.status.value
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(parseNetworkError(e))
        }
    }

    /**
     * Selecciona (bloquea) asientos en la sesión actual.
     * Bloqueo válido por 5 minutos.
     *
     * @param asientos Lista de coordenadas de asientos a bloquear
     * @return NetworkResult con sesión actualizada o error
     */
    suspend fun selectSeats(asientos: List<SeatCoordinates>): NetworkResult<SessionResponse> {
        return try {
            val response = httpClient.post(Endpoints.COMPRA_SELECCIONAR_ASIENTOS) {
                contentType(ContentType.Application.Json)
                setBody(SelectSeatsRequest(asientos))
            }

            when (response.status) {
                HttpStatusCode.OK -> {
                    NetworkResult.Success(response.body<SessionResponse>())
                }
                HttpStatusCode.BadRequest -> {
                    NetworkResult.Error("Asientos no disponibles", 400)
                }
                HttpStatusCode.Conflict -> {
                    NetworkResult.Error("Uno o más asientos ya están reservados", 409)
                }
                HttpStatusCode.Unauthorized -> {
                    NetworkResult.Error("Sesión expirada", 401)
                }
                else -> {
                    NetworkResult.Error(
                        "Error al seleccionar asientos: ${response.status.description}",
                        response.status.value
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(parseNetworkError(e))
        }
    }

    /**
     * Obtiene el estado actual de la sesión de compra.
     *
     * @return NetworkResult con la sesión actual o error
     */
    suspend fun getSessionState(): NetworkResult<SessionResponse> {
        return try {
            val response = httpClient.get(Endpoints.COMPRA_ESTADO)

            when (response.status) {
                HttpStatusCode.OK -> {
                    NetworkResult.Success(response.body<SessionResponse>())
                }
                HttpStatusCode.NotFound -> {
                    NetworkResult.Error("No hay sesión activa", 404)
                }
                HttpStatusCode.Unauthorized -> {
                    NetworkResult.Error("Sesión expirada", 401)
                }
                else -> {
                    NetworkResult.Error(
                        "Error al obtener estado: ${response.status.description}",
                        response.status.value
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(parseNetworkError(e))
        }
    }

    /**
     * Cancela la sesión de compra actual.
     * Libera todos los asientos bloqueados.
     *
     * @return NetworkResult con éxito o error
     */
    suspend fun cancelSession(): NetworkResult<Unit> {
        return try {
            val response = httpClient.delete(Endpoints.COMPRA_CANCELAR)

            when (response.status) {
                HttpStatusCode.OK, HttpStatusCode.NoContent -> {
                    NetworkResult.Success(Unit)
                }
                HttpStatusCode.NotFound -> {
                    NetworkResult.Error("No hay sesión activa", 404)
                }
                HttpStatusCode.Unauthorized -> {
                    NetworkResult.Error("Sesión expirada", 401)
                }
                else -> {
                    NetworkResult.Error(
                        "Error al cancelar sesión: ${response.status.description}",
                        response.status.value
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(parseNetworkError(e))
        }
    }

    /**
     * Asigna nombres a los asientos seleccionados.
     *
     * @param nombres Map de "fila-columna" -> "Nombre Completo"
     * @return NetworkResult con sesión actualizada o error
     */
    suspend fun assignNames(nombres: Map<String, String>): NetworkResult<SessionResponse> {
        return try {
            val response = httpClient.post(Endpoints.COMPRA_ASIGNAR_NOMBRES) {
                contentType(ContentType.Application.Json)
                setBody(AssignNamesRequest(nombres))
            }
            when (response.status) {
                HttpStatusCode.OK -> {
                    NetworkResult.Success(response.body<SessionResponse>())
                }
                HttpStatusCode.BadRequest -> {
                    NetworkResult.Error("Nombres inválidos o incompletos", 400)
                }
                HttpStatusCode.Unauthorized -> {
                    NetworkResult.Error("Sesión expirada", 401)
                }
                else -> {
                    NetworkResult.Error(
                        "Error al asignar nombres: ${response.status.description}",
                        response.status.value
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(parseNetworkError(e))
        }
    }

    /**
     * Confirma la compra final de los asientos.
     *
     * Endpoint: POST /api/compra/confirmar
     * Request: Ninguno (usa sesión del usuario autenticado)
     * Response: VentaDTO con el resultado de la venta
     *
     * Validaciones del backend:
     * - Debe haber asientos seleccionados
     * - Todos los asientos deben tener nombre asignado
     * - Los bloqueos deben seguir vigentes (o se re-bloquean)
     *
     * @return NetworkResult con VentaResponse o error
     */
    suspend fun confirmPurchase(): NetworkResult<VentaResponse> {
        return try {
            val response = httpClient.post(Endpoints.COMPRA_CONFIRMAR) {
                contentType(ContentType.Application.Json)
                // No se envía body - usa la sesión del usuario autenticado
            }
            when (response.status) {
                HttpStatusCode.OK -> {
                    NetworkResult.Success(response.body<VentaResponse>())
                }
                HttpStatusCode.BadRequest -> {
                    NetworkResult.Error(
                        "No hay asientos seleccionados o faltan nombres asignados",
                        400
                    )
                }
                HttpStatusCode.Conflict -> {
                    NetworkResult.Error(
                        "Sesión expirada o asientos ya vendidos por otro usuario",
                        409
                    )
                }
                HttpStatusCode.Unauthorized -> {
                    NetworkResult.Error("Sesión expirada. Por favor inicia sesión nuevamente", 401)
                }
                else -> {
                    NetworkResult.Error(
                        "Error al confirmar compra: ${response.status.description}",
                        response.status.value
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(parseNetworkError(e))
        }
    }

    /**
     * Parser de errores de red genérico.
     * Convierte excepciones técnicas en mensajes user-friendly.
     */
    private fun parseNetworkError(e: Exception): String {
        return when {
            e.message?.contains("Unable to resolve host", ignoreCase = true) == true ||
                    e.message?.contains("Failed to connect", ignoreCase = true) == true -> {
                "No hay conexión con el servidor"
            }
            e.message?.contains("timeout", ignoreCase = true) == true -> {
                "El servidor tardó demasiado en responder"
            }
            else -> {
                "Error de red: ${e.message ?: "Desconocido"}"
            }
        }
    }
}