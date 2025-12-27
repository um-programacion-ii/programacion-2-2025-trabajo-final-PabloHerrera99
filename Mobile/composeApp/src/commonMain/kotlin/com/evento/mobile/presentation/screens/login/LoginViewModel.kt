package com.evento.mobile.presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evento.mobile.data.repository.AuthRepository
import com.evento.mobile.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Login.
 *
 * Este ViewModel maneja toda la lógica de negocio de la pantalla de login:
 * - Gestión del estado de la UI (username, password, loading, errors)
 * - Validaciones de campos
 * - Comunicación con el AuthRepository para autenticación
 * - Actualización reactiva del estado para la UI
 *
 * Sigue el patrón MVVM (Model-View-ViewModel) y usa StateFlow
 * para comunicación reactiva con la UI.
 */
class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    /**
     * Estado mutable privado.
     * Solo el ViewModel puede modificarlo.
     */
    private val _uiState = MutableStateFlow(LoginUiState())

    /**
     * Estado público de solo lectura.
     * La UI observa este StateFlow para renderizarse reactivamente.
     */
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Actualiza el username ingresado por el usuario.
     *
     * Se llama cada vez que el usuario escribe en el campo de usuario.
     * Limpia cualquier error previo para dar feedback inmediato.
     *
     * @param username Nuevo texto del campo de usuario
     */
    fun onUsernameChange(username: String) {
        _uiState.update { currentState ->
            currentState.copy(
                username = username,
                error = null  // Limpiar error al escribir
            )
        }
    }

    /**
     * Actualiza la password ingresada por el usuario.
     *
     * Se llama cada vez que el usuario escribe en el campo de contraseña.
     * Limpia cualquier error previo para dar feedback inmediato.
     *
     * @param password Nuevo texto del campo de contraseña
     */
    fun onPasswordChange(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = password,
                error = null  // Limpiar error al escribir
            )
        }
    }

    /**
     * Ejecuta el proceso de login con validaciones.
     *
     * Flujo:
     * 1. Validar que los campos no estén vacíos
     * 2. Mostrar indicador de carga
     * 3. Llamar al repository para autenticar
     * 4. Actualizar estado según resultado (éxito o error)
     *
     * Este método es invocado cuando el usuario hace click en
     * el botón "Iniciar Sesión".
     */
    fun onLoginClick() {
        val currentState = _uiState.value

        // Validación 1: Username no puede estar vacío
        if (currentState.username.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa tu usuario") }
            return
        }

        // Validación 2: Password no puede estar vacía
        if (currentState.password.isBlank()) {
            _uiState.update { it.copy(error = "Ingresa tu contraseña") }
            return
        }

        // Todas las validaciones pasaron, iniciar login
        viewModelScope.launch {
            // Mostrar indicador de carga
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Llamar al repository para autenticar
            val result = authRepository.login(
                username = currentState.username,
                password = currentState.password
            )

            // Actualizar estado según el resultado
            when (result) {
                is NetworkResult.Success -> {
                    // Login exitoso
                    _uiState.update { it.copy(
                        isLoading = false,
                        isLoginSuccessful = true,
                        error = null
                    )}
                }

                is NetworkResult.Error -> {
                    // Error en login (credenciales incorrectas, servidor caído, etc.)
                    _uiState.update { it.copy(
                        isLoading = false,
                        error = result.message
                    )}
                }

                is NetworkResult.Loading -> {
                }
            }
        }
    }

    /**
     * Resetea el flag de login exitoso.
     *
     * Se llama después de navegar a la siguiente pantalla para evitar
     * navegaciones múltiples si el estado se recompone.
     */
    fun resetLoginSuccess() {
        _uiState.update { it.copy(isLoginSuccessful = false) }
    }
}
