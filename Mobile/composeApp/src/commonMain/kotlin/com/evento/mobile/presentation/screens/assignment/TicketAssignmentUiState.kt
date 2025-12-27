package com.evento.mobile.presentation.screens.assignment

/**
 * Estados de la pantalla de asignación de nombres.
 */
sealed interface TicketAssignmentUiState {

    /**
     * Estado inicial de carga.
     */
    data object Loading : TicketAssignmentUiState

    /**
     * Estado exitoso con lista de asientos para asignar nombres.
     *
     * @param seats Lista de asientos con sus nombres (editable)
     * @param isSubmitting True si está enviando datos al backend
     * @param timeRemainingSeconds Segundos restantes antes de que expire el bloqueo (300s = 5min)
     */
    data class Success(
        val seats: List<SeatWithName>,
        val isSubmitting: Boolean = false,
        val timeRemainingSeconds: Int = 300 // 5 minutos
    ) : TicketAssignmentUiState

    /**
     * Estado de error.
     */
    data class Error(val message: String) : TicketAssignmentUiState
}

/**
 * Representa un asiento con su nombre asignado.
 *
 * @param fila Número de fila (1-based)
 * @param columna Número de columna (1-based)
 * @param nombre Nombre de la persona (vacío inicialmente)
 */
data class SeatWithName(
    val fila: Int,
    val columna: Int,
    val nombre: String = ""
) {
    /**
     * ID del asiento en formato "fila-columna" (ej: "1-5")
     */
    val seatId: String
        get() = "$fila-$columna"

    /**
     * Validación: nombre debe tener al menos 3 caracteres.
     */
    val isValid: Boolean
        get() = nombre.trim().length >= 3
}