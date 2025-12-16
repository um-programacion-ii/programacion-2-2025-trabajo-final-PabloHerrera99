package com.evento.mobile.presentation.screens.confirmation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evento.mobile.data.repository.PurchaseRepository
import com.evento.mobile.presentation.screens.assignment.SeatWithName
import com.evento.mobile.presentation.screens.confirmation.PurchaseConfirmationUiState.*
import com.evento.mobile.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de confirmación de compra.
 *
 * Responsabilidades:
 * - Confirmar compra automáticamente al inicializarse
 * - Manejar estados (Loading, Success, Error)
 * - Permitir reintentar en caso de error
 */
class PurchaseConfirmationViewModel(
    private val purchaseRepository: PurchaseRepository,
    private val selectedSeats: List<SeatWithName>
) : ViewModel() {
    private val _uiState = MutableStateFlow<PurchaseConfirmationUiState>(
        Loading
    )
    val uiState: StateFlow<PurchaseConfirmationUiState> = _uiState.asStateFlow()
    init {
        confirmPurchase()
    }
    fun confirmPurchase() {
        viewModelScope.launch {
            _uiState.value = Loading

            when (val result = purchaseRepository.confirmPurchase()) {
                is NetworkResult.Success -> {
                    if (result.data.exitosa == true) {
                        _uiState.value = Success
                    } else {
                        _uiState.value = Error(
                            message = result.data.descripcion
                                ?: "La compra no pudo ser procesada",
                            canRetry = false
                        )
                    }
                }

                is NetworkResult.Error -> {
                    _uiState.value = Error(
                        message = when (result.code) {
                            401 -> "Sesión expirada. Por favor, inicia sesión nuevamente."
                            409 -> "Conflicto con la compra. Los asientos ya no están disponibles."
                            else -> result.message ?: "Error de conexión"
                        },
                        canRetry = result.code !in listOf(401, 409)
                    )
                }

                is NetworkResult.Loading -> {
                    // No hacer nada, ya estamos en Loading state
                    // Esta rama probablemente nunca se ejecuta porque confirmPurchase()
                    // es suspend y solo devuelve Success o Error
                }
            }
        }
    }
}
