package com.evento.mobile.presentation.screens.login

/**
 * Estado de la UI de la pantalla de Login.
 *
 * Esta data class representa todos los estados posibles de la
 * pantalla de login usando el patrón de UI State.
 *
 * El ViewModel mantiene este estado en un StateFlow y la UI
 * se renderiza reactivamente basándose en este estado.
 *
 * @property username Texto ingresado en el campo de usuario
 * @property password Texto ingresado en el campo de contraseña
 * @property isLoading Indica si hay una operación de login en progreso
 * @property error Mensaje de error a mostrar (null si no hay error)
 * @property isLoginSuccessful Indica si el login fue exitoso (para navegar)
 */
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLoginSuccessful: Boolean = false
)