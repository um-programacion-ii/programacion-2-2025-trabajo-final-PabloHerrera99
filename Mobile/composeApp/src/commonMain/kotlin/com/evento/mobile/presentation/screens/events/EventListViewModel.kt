package com.evento.mobile.presentation.screens.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evento.mobile.data.repository.AuthRepository
import com.evento.mobile.data.repository.EventRepository
import com.evento.mobile.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
/**
 * ViewModel para la pantalla de lista de eventos.
 *
 * Responsabilidades:
 * - Cargar eventos del repositorio
 * - Manejar paginación (scroll infinito)
 * - Manejar refresh (pull-to-refresh)
 * - Exponer estado de UI como StateFlow
 */
class EventListViewModel(
    private val eventRepository: EventRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EventListUiState())
    val uiState: StateFlow<EventListUiState> = _uiState.asStateFlow()
    init {
        loadEventos()
    }
    /**
     * Carga la primera página de eventos.
     *
     * @param forceRefresh Si true, ignora caché y hace fetch nuevo
     */
    fun loadEventos(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = !forceRefresh,
                    isRefreshing = forceRefresh
                )
            }
            when (val result = eventRepository.getEventos(page = 0, size = 20, forceRefresh = forceRefresh)) {
                is NetworkResult.Success -> {
                    val (eventos, totalCount) = result.data
                    _uiState.update {
                        it.copy(
                            eventos = eventos,
                            totalCount = totalCount,
                            currentPage = 0,
                            hasMorePages = eventos.size < totalCount,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            error = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> { /* Estado ya actualizado */ }
            }
        }
    }
    /**
     * Carga la siguiente página de eventos (scroll infinito).
     * Solo se ejecuta si no está ya cargando y hay más páginas.
     */
    fun loadMoreEventos() {
        val currentState = _uiState.value
        if (currentState.isLoadingMore || !currentState.hasMorePages) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            val nextPage = currentState.currentPage + 1
            when (val result = eventRepository.getEventos(page = nextPage, size = 20)) {
                is NetworkResult.Success -> {
                    val (newEventos, totalCount) = result.data
                    val allEventos = currentState.eventos + newEventos

                    _uiState.update {
                        it.copy(
                            eventos = allEventos,
                            currentPage = nextPage,
                            hasMorePages = allEventos.size < totalCount,
                            isLoadingMore = false
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoadingMore = false,
                            error = result.message
                        )
                    }
                }
                is NetworkResult.Loading -> { /* Estado ya actualizado */ }
            }
        }
    }
    /**
     * Refresh manual (pull-to-refresh).
     */
    fun refresh() {
        loadEventos(forceRefresh = true)
    }
    /**
     * Limpia el mensaje de error.
     */
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
    /**
     * Logout del usuario.
     * Limpia token y caché.
     */
    fun logout() {
        authRepository.logout()
        eventRepository.clearCache()
    }
}