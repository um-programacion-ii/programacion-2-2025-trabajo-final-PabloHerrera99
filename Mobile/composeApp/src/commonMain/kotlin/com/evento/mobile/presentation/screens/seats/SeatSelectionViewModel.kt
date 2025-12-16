package com.evento.mobile.presentation.screens.seats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evento.mobile.data.model.purchase.SeatCoordinates
import com.evento.mobile.data.repository.EventRepository
import com.evento.mobile.data.repository.PurchaseRepository
import com.evento.mobile.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de selección de asientos.
 *
 * Responsabilidades:
 * - Iniciar sesión de compra al entrar a la pantalla
 * - Cargar matriz de asientos disponibles
 * - Gestionar selección local de asientos (máximo 4)
 * - Bloquear asientos seleccionados en el backend
 * - Manejar errores y mensajes al usuario
 */
class SeatSelectionViewModel(
    private val eventRepository: EventRepository,
    private val purchaseRepository: PurchaseRepository,
    private val eventoId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow<SeatSelectionUiState>(SeatSelectionUiState.Loading)
    val uiState: StateFlow<SeatSelectionUiState> = _uiState.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    companion object {
        const val MAX_SEATS = 4
    }

    init {
        startSessionAndLoadSeats()
    }

    /**
     * Inicia sesión de compra y carga matriz de asientos.
     * Se ejecuta automáticamente al crear el ViewModel.
     */
    private fun startSessionAndLoadSeats() {
        viewModelScope.launch {
            _uiState.value = SeatSelectionUiState.Loading

            // 1. Iniciar sesión de compra
            when (val sessionResult = purchaseRepository.startSession(eventoId)) {
                is NetworkResult.Success -> {
                    val sessionId = sessionResult.data.id

                    // 2. Cargar matriz de asientos
                    loadSeatMatrix(sessionId)
                }
                is NetworkResult.Error -> {
                    _uiState.value = SeatSelectionUiState.Error(sessionResult.message)
                }
                else -> {}
            }
        }
    }

    /**
     * Carga la matriz de asientos del evento.
     */
    private suspend fun loadSeatMatrix(sessionId: String) {
        when (val matrixResult = eventRepository.getDisponibilidadAsientos(eventoId)) {
            is NetworkResult.Success -> {
                _uiState.value = SeatSelectionUiState.Success(
                    matriz = matrixResult.data,
                    sessionId = sessionId
                )
            }
            is NetworkResult.Error -> {
                _uiState.value = SeatSelectionUiState.Error(matrixResult.message)
            }
            else -> {}
        }
    }

    /**
     * Alterna la selección de un asiento.
     * Si está seleccionado, lo deselecciona. Si no, lo selecciona (máximo 4).
     *
     * @param seatId ID del asiento en formato "fila-columna" (ej: "1-5")
     */
    fun toggleSeat(seatId: String) {
        val currentState = _uiState.value
        if (currentState !is SeatSelectionUiState.Success || currentState.isBlocking) return

        val selectedSeats = currentState.selectedSeats.toMutableSet()

        if (seatId in selectedSeats) {
            // Deseleccionar
            selectedSeats.remove(seatId)
        } else {
            // Intentar seleccionar
            if (selectedSeats.size >= MAX_SEATS) {
                _snackbarMessage.value = "Máximo $MAX_SEATS asientos por compra"
                return
            }
            selectedSeats.add(seatId)
        }

        _uiState.value = currentState.copy(selectedSeats = selectedSeats)
    }

    /**
     * Bloquea los asientos seleccionados en el backend y navega a siguiente pantalla.
     *
     * @param onSuccess Callback con sessionId si el bloqueo fue exitoso
     */
    fun blockSeatsAndContinue(onSuccess: (List<SeatCoordinates>) -> Unit) {
        val currentState = _uiState.value
        if (currentState !is SeatSelectionUiState.Success) return

        if (currentState.selectedSeats.isEmpty()) {
            _snackbarMessage.value = "Selecciona al menos un asiento"
            return
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isBlocking = true)

            // Convertir IDs "fila-columna" a SeatCoordinates
            val seats = currentState.selectedSeats.map { seatId ->
                val parts = seatId.split("-")
                SeatCoordinates(fila = parts[0].toInt(), columna = parts[1].toInt())
            }

            when (val result = purchaseRepository.selectSeats(seats)) {
                is NetworkResult.Success -> {
                    onSuccess(seats)
                }
                is NetworkResult.Error -> {
                    _uiState.value = currentState.copy(isBlocking = false)
                    _snackbarMessage.value = result.message
                }
                else -> {}
            }
        }
    }

    /**
     * Reintenta iniciar sesión y cargar asientos.
     * Usado cuando ocurre un error inicial.
     */
    fun retry() {
        startSessionAndLoadSeats()
    }

    /**
     * Limpia el mensaje de snackbar actual.
     */
    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}