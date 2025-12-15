package com.evento.mobile.presentation.screens.seats

import com.evento.mobile.data.model.seat.MatrizAsientosResponse

/**
 * Estados posibles de la pantalla de selección de asientos.
 */
sealed class SeatSelectionUiState {
    /**
     * Estado inicial: cargando sesión y matriz de asientos.
     */
    data object Loading : SeatSelectionUiState()

    /**
     * Estado exitoso: sesión creada y asientos disponibles para selección.
     *
     * @property matriz Información completa de asientos del evento
     * @property sessionId ID de la sesión de compra activa
     * @property selectedSeats Set de IDs de asientos seleccionados localmente (formato "fila-columna")
     * @property isBlocking True si está en proceso de bloqueo de asientos
     */
    data class Success(
        val matriz: MatrizAsientosResponse,
        val sessionId: String,
        val selectedSeats: Set<String> = emptySet(),
        val isBlocking: Boolean = false
    ) : SeatSelectionUiState()

    /**
     * Estado de error: fallo al crear sesión o cargar asientos.
     * @property message Mensaje de error a mostrar al usuario
     */
    data class Error(val message: String) : SeatSelectionUiState()
}