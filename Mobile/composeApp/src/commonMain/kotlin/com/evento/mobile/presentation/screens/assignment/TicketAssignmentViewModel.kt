package com.evento.mobile.presentation.screens.assignment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evento.mobile.data.model.purchase.SeatCoordinates
import com.evento.mobile.data.repository.PurchaseRepository
import com.evento.mobile.util.NetworkResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
/**
 * ViewModel para la pantalla de asignación de nombres a tickets.
 *
 * Responsabilidades:
 * - Convertir asientos seleccionados a lista editable
 * - Validar nombres (mínimo 3 caracteres)
 * - Gestionar temporizador de expiración (5 minutos)
 * - Enviar nombres al backend
 * - Manejar errores y navegación
 */
class TicketAssignmentViewModel(
    private val purchaseRepository: PurchaseRepository,
    selectedSeats: List<SeatCoordinates>
) : ViewModel() {
    private val _uiState = MutableStateFlow<TicketAssignmentUiState>(
        TicketAssignmentUiState.Success(
            seats = selectedSeats.map { seat ->
                SeatWithName(fila = seat.fila, columna = seat.columna)
            }
        )
    )
    val uiState: StateFlow<TicketAssignmentUiState> = _uiState.asStateFlow()
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()
    private var countdownJob: Job? = null
    companion object {
        const val MIN_NAME_LENGTH = 3
        const val TOTAL_SECONDS = 300 // 5 minutos
    }
    init {
        startCountdownTimer()
    }
    /**
     * Inicia el temporizador de cuenta regresiva (5 minutos).
     * Actualiza cada segundo el tiempo restante.
     */
    private fun startCountdownTimer() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            var secondsRemaining = TOTAL_SECONDS

            while (secondsRemaining > 0) {
                delay(1000) // 1 segundo
                secondsRemaining--

                val currentState = _uiState.value
                if (currentState is TicketAssignmentUiState.Success) {
                    _uiState.value = currentState.copy(
                        timeRemainingSeconds = secondsRemaining
                    )
                }

                // Advertencia cuando quedan 60 segundos
                if (secondsRemaining == 60) {
                    _snackbarMessage.value = "⚠️ Quedan 60 segundos antes de que expire el bloqueo"
                }
            }

            // Tiempo expirado
            _snackbarMessage.value = "El tiempo expiró. Los asientos han sido liberados."
        }
    }
    /**
     * Actualiza el nombre de un asiento específico.
     *
     * @param fila Número de fila
     * @param columna Número de columna
     * @param nombre Nuevo nombre
     */
    fun updateName(fila: Int, columna: Int, nombre: String) {
        val currentState = _uiState.value
        if (currentState !is TicketAssignmentUiState.Success || currentState.isSubmitting) return
        val updatedSeats = currentState.seats.map { seat ->
            if (seat.fila == fila && seat.columna == columna) {
                seat.copy(nombre = nombre)
            } else {
                seat
            }
        }
        _uiState.value = currentState.copy(seats = updatedSeats)
    }
    /**
     * Valida y envía los nombres al backend.
     * Solo se ejecuta si todos los nombres son válidos.
     *
     * @param onSuccess Callback ejecutado si el POST es exitoso
     */
    fun submitNames(onSuccess: (List<SeatWithName>) -> Unit) {  // ← Cambio aquí
        val currentState = _uiState.value
        if (currentState !is TicketAssignmentUiState.Success) return

        if (!areAllNamesValid(currentState.seats)) {
            _snackbarMessage.value = "Todos los nombres deben tener al menos $MIN_NAME_LENGTH caracteres"
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isSubmitting = true)

            val nombresMap = currentState.seats.associate { seat ->
                seat.seatId to seat.nombre.trim()
            }

            when (val result = purchaseRepository.assignNames(nombresMap)) {
                is NetworkResult.Success -> {
                    countdownJob?.cancel()
                    onSuccess(currentState.seats)  // ← Pasar asientos
                }
                is NetworkResult.Error -> {
                    _uiState.value = currentState.copy(isSubmitting = false)
                    _snackbarMessage.value = result.message
                }
                else -> {}
            }
        }
    }
    /**
     * Verifica si todos los nombres son válidos (>= 3 caracteres).
     */
    private fun areAllNamesValid(seats: List<SeatWithName>): Boolean {
        return seats.all { it.isValid }
    }
    /**
     * Limpia el mensaje del snackbar.
     */
    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
    /**
     * Cancela el temporizador al destruir el ViewModel.
     */
    override fun onCleared() {
        super.onCleared()
        countdownJob?.cancel()
    }
}