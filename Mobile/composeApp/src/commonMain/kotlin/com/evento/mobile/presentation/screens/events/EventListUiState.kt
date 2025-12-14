package com.evento.mobile.presentation.screens.events

import com.evento.mobile.data.model.event.EventoResponse
/**
 * Estado de UI para la pantalla de lista de eventos.
 *
 * Maneja todos los estados posibles:
 * - Loading inicial
 * - Refreshing (pull-to-refresh)
 * - Loading more (scroll infinito)
 * - Success con datos
 * - Error
 */
data class EventListUiState(
    /**
     * Lista de eventos cargados.
     */
    val eventos: List<EventoResponse> = emptyList(),

    /**
     * True cuando está cargando la primera página (spinner central).
     */
    val isLoading: Boolean = false,

    /**
     * True cuando está haciendo pull-to-refresh.
     */
    val isRefreshing: Boolean = false,

    /**
     * True cuando está cargando más eventos (scroll infinito).
     */
    val isLoadingMore: Boolean = false,

    /**
     * Mensaje de error si algo falló.
     */
    val error: String? = null,

    /**
     * Página actual (0-based).
     */
    val currentPage: Int = 0,

    /**
     * Total de eventos en el servidor.
     */
    val totalCount: Long = 0,

    /**
     * True si hay más páginas para cargar.
     */
    val hasMorePages: Boolean = true
)