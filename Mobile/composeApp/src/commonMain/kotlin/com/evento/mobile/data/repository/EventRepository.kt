package com.evento.mobile.data.repository

import com.evento.mobile.data.model.event.EventoResponse
import com.evento.mobile.data.model.seat.MatrizAsientosResponse
import com.evento.mobile.data.remote.EventApiService
import com.evento.mobile.util.NetworkResult
/**
 * Repositorio para eventos.
 *
 * Actúa como capa intermedia entre ViewModels y EventApiService.
 * Responsabilidades:
 * - Caché simple de eventos en memoria
 * - Lógica de cuándo usar caché vs fetch nuevo
 * - Abstracción de la capa de red
 */
class EventRepository(private val apiService: EventApiService) {
    /**
     * Caché simple en memoria de eventos.
     * Evita re-fetching innecesario cuando el usuario navega entre pantallas.
     */
    private var cachedEventos: List<EventoResponse>? = null
    private var lastFetchTime: Long = 0

    // Caché válido por 1 minuto
    private val CACHE_VALIDITY_MS = 60_000
    /**
     * Obtiene lista de eventos con soporte de caché.
     *
     * @param page Número de página (0-based)
     * @param size Cantidad de eventos por página
     * @param forceRefresh Si true, ignora caché y hace fetch nuevo
     * @return NetworkResult con par (lista de eventos, total count)
     */
    suspend fun getEventos(
        page: Int = 0,
        size: Int = 20,
        forceRefresh: Boolean = false
    ): NetworkResult<Pair<List<EventoResponse>, Long>> {

        // Si hay caché válido y no se fuerza refresh, retornar caché
        if (!forceRefresh &&
            cachedEventos != null &&
            (System.currentTimeMillis() - lastFetchTime) < CACHE_VALIDITY_MS) {
            return NetworkResult.Success(Pair(cachedEventos!!, cachedEventos!!.size.toLong()))
        }
        // Hacer fetch real al backend
        return when (val result = apiService.getEventos(page, size)) {
            is NetworkResult.Success -> {
                // Actualizar caché solo si es la primera página
                if (page == 0) {
                    cachedEventos = result.data.first
                    lastFetchTime = System.currentTimeMillis()
                }
                result
            }
            is NetworkResult.Error -> result
            is NetworkResult.Loading -> result
        }
    }
    /**
     * Obtiene detalle de un evento específico.
     *
     * @param id ID del evento
     * @return NetworkResult con el evento
     */
    suspend fun getEvento(id: Long): NetworkResult<EventoResponse> {
        return apiService.getEvento(id)
    }
    /**
     * Obtiene disponibilidad de asientos de un evento.
     *
     * @param eventoId ID del evento
     * @return NetworkResult con la matriz de asientos
     */
    suspend fun getDisponibilidadAsientos(eventoId: Long): NetworkResult<MatrizAsientosResponse> {
        return apiService.getDisponibilidadAsientos(eventoId)
    }
    /**
     * Limpia el caché de eventos.
     * Útil cuando el usuario hace logout.
     */
    fun clearCache() {
        cachedEventos = null
        lastFetchTime = 0
    }
}