package com.evento.mobile.presentation.screens.detail

import com.evento.mobile.data.model.event.EventoResponse
sealed class EventDetailUiState {
    // Estado inicial: está cargando
    data object Loading : EventDetailUiState()

    // Estado exitoso: tenemos los datos
    data class Success(
        val evento: EventoResponse,
        val asientosDisponibles: Int,
        val asientosTotales: Int
    ) : EventDetailUiState() {
        // Propiedad calculada: porcentaje de ocupación
        val porcentajeOcupado: Int
            get() = if (asientosTotales > 0) {
                ((asientosTotales - asientosDisponibles) * 100 / asientosTotales)
            } else {
                0
            }
    }

    // Estado de error: algo falló
    data class Error(val message: String) : EventDetailUiState()
}