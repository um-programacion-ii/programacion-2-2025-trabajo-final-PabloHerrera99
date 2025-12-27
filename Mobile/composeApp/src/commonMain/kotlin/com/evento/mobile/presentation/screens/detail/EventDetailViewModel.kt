package com.evento.mobile.presentation.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evento.mobile.data.model.seat.EstadoAsiento
import com.evento.mobile.data.repository.EventRepository
import com.evento.mobile.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class EventDetailViewModel(
    private val eventRepository: EventRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<EventDetailUiState>(EventDetailUiState.Loading)
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()
    fun loadEventDetail(eventId: Long) {
        viewModelScope.launch {
            // 1. Empezar en estado Loading
            _uiState.value = EventDetailUiState.Loading
            // 2. Cargar el evento desde el backend
            val eventoResult = eventRepository.getEvento(eventId)
            if (eventoResult !is NetworkResult.Success) {
                // Si falla, cambiar a estado Error
                val errorMessage = (eventoResult as? NetworkResult.Error)?.message
                    ?: "Error al cargar el evento"
                _uiState.value = EventDetailUiState.Error(errorMessage)
                return@launch
            }
            val evento = eventoResult.data
            // 3. Cargar disponibilidad de asientos
            val disponibilidadResult = eventRepository.getDisponibilidadAsientos(eventId)
            if (disponibilidadResult !is NetworkResult.Success) {
                val errorMessage = (disponibilidadResult as? NetworkResult.Error)?.message
                    ?: "Error al cargar disponibilidad de asientos"
                _uiState.value = EventDetailUiState.Error(errorMessage)
                return@launch
            }
            // 4. Obtener disponibilidad (ya calculada por el backend)
            val matriz = disponibilidadResult.data
            // 5. Actualizar a estado Success
            _uiState.value = EventDetailUiState.Success(
                evento = evento,
                asientosDisponibles = matriz.disponibles,
                asientosTotales = matriz.totalAsientos
            )
        }
    }
}