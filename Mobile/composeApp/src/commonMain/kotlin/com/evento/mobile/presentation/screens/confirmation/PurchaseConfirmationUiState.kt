package com.evento.mobile.presentation.screens.confirmation

/**
 * Estados de la pantalla de confirmación de compra.
 */
sealed interface PurchaseConfirmationUiState {

    /**
     * Estado de carga: confirmando compra en el servidor.
     */
    data object Loading : PurchaseConfirmationUiState

    data object Success : PurchaseConfirmationUiState

    /**
     * Estado de error: falló la confirmación.
     *
     * @param message Mensaje de error a mostrar al usuario
     * @param canRetry True si se puede reintentar (false para errores permanentes como 409)
     */
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : PurchaseConfirmationUiState
}