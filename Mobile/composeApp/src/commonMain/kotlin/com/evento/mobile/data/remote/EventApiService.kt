package com.evento.mobile.data.remote

import com.evento.mobile.data.model.event.EventoResponse
import com.evento.mobile.data.model.seat.MatrizAsientosResponse
import com.evento.mobile.di.Endpoints
import com.evento.mobile.util.NetworkResult
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
/**
 * Servicio de API para operaciones relacionadas con eventos.
 *
 * Encapsula todas las llamadas HTTP al backend para:
 * - Obtener lista de eventos
 * - Obtener detalle de un evento
 * - Obtener disponibilidad de asientos
 */
class EventApiService(private val httpClient: HttpClient) {
    /**
     * Obtiene lista paginada de eventos.
     *
     * @param page Número de página (0-based)
     * @param size Cantidad de elementos por página
     * @param activoOnly Si true, filtra solo eventos activos
     * @param conIdCatedraOnly Si true, filtra solo eventos con idCatedra
     * @return NetworkResult con par (lista de eventos, total count) o error
     */
    suspend fun getEventos(
        page: Int = 0,
        size: Int = 20,
        activoOnly: Boolean = true,
        conIdCatedraOnly: Boolean = true
    ): NetworkResult<Pair<List<EventoResponse>, Long>> {
        return try {
            // Construir query params dinámicamente
            val response = httpClient.get(Endpoints.EVENTOS) {
                parameter("page", page)
                parameter("size", size)
                parameter("sort", "fecha,desc") // Ordenar por fecha descendente

                if (activoOnly) {
                    parameter("activo.equals", "true")
                }
                if (conIdCatedraOnly) {
                    parameter("idCatedra.specified", "true")
                }
            }
            when (response.status) {
                HttpStatusCode.OK -> {
                    val eventos = response.body<List<EventoResponse>>()

                    // Leer header X-Total-Count para paginación
                    val totalCount = response.headers["X-Total-Count"]?.toLongOrNull()
                        ?: eventos.size.toLong()

                    NetworkResult.Success(Pair(eventos, totalCount))
                }
                HttpStatusCode.Unauthorized -> {
                    NetworkResult.Error("Sesión expirada. Inicia sesión nuevamente", 401)
                }
                else -> {
                    NetworkResult.Error(
                        "Error al obtener eventos: ${response.status.description}",
                        response.status.value
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(parseNetworkError(e))
        }
    }
    /**
     * Obtiene un evento específico por ID.
     *
     * @param id ID del evento
     * @return NetworkResult con el evento o error
     */
    suspend fun getEvento(id: Long): NetworkResult<EventoResponse> {
        return try {
            val response = httpClient.get("${Endpoints.EVENTOS}/$id")
            when (response.status) {
                HttpStatusCode.OK -> {
                    NetworkResult.Success(response.body<EventoResponse>())
                }
                HttpStatusCode.NotFound -> {
                    NetworkResult.Error("Evento no encontrado", 404)
                }
                HttpStatusCode.Unauthorized -> {
                    NetworkResult.Error("Sesión expirada", 401)
                }
                else -> {
                    NetworkResult.Error(
                        "Error al obtener evento: ${response.status.description}",
                        response.status.value
                    )
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(parseNetworkError(e))
        }
    }
    /**
     * Obtiene disponibilidad de asientos de un evento.
     *
     * @param eventoId ID del evento
     * @return NetworkResult con la matriz de asientos o error
     */
    suspend fun getDisponibilidadAsientos(eventoId: Long): NetworkResult<MatrizAsientosResponse> {
        return try {
            val response = httpClient.get("${Endpoints.EVENTOS}/$eventoId/asientos/disponibilidad")
            when (response.status) {
                HttpStatusCode.OK -> {
                    NetworkResult.Success(response.body<MatrizAsientosResponse>())
                }
                HttpStatusCode.NotFound -> {
                    NetworkResult.Error("Evento no encontrado", 404)
                }
                HttpStatusCode.BadRequest -> {
                    NetworkResult.Error("Evento sin configuración de asientos", 400)
                }
                HttpStatusCode.Unauthorized -> {
                    NetworkResult.Error("Sesión expirada", 401)
                }
                else -> {
                    NetworkResult.Error(
                        "Error al obtener disponibilidad: ${response.status.description}",
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