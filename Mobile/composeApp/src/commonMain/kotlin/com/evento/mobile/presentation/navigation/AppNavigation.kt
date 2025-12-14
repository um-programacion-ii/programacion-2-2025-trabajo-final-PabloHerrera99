package com.evento.mobile.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.evento.mobile.data.remote.AuthApiService
import com.evento.mobile.data.repository.AuthRepository
import com.evento.mobile.di.HttpClientProvider
import com.evento.mobile.presentation.screens.events.EventListScreen
import com.evento.mobile.presentation.screens.login.LoginScreen
import com.evento.mobile.presentation.screens.login.LoginViewModel

/**
 * Configuración principal de navegación de la aplicación.
 *
 * Este composable actúa como el punto de entrada de la navegación,
 * coordinando:
 * - Inicialización de dependencias (DI manual)
 * - Configuración del NavHost
 * - Definición de rutas y pantallas
 * - Lógica de navegación entre pantallas
 */
@Composable
fun AppNavigation() {
    // Controlador de navegación
    val navController = rememberNavController()

    // Inicialización de dependencias (DI manual)

    // 1. Crear HttpClient configurado
    val httpClient = HttpClientProvider.create()

    // 2. Crear AuthApiService con el HttpClient
    val authApiService = AuthApiService(httpClient)

    // 3. Crear AuthRepository con el AuthApiService
    val authRepository = AuthRepository(authApiService)

    // 4. Configurar el proveedor de token para HttpClient
    HttpClientProvider.setTokenProvider { authRepository.getToken() }

    // 5. Crear LoginViewModel con el AuthRepository
    val loginViewModel = LoginViewModel(authRepository)

    // Configuración de navegación

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route  // Pantalla inicial: Login
    ) {
        // Ruta: "login"
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    // Navegar a EventList cuando el login sea exitoso
                    navController.navigate(Screen.EventList.route) {
                        // Limpiar el back stack para que no se pueda volver al login
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Ruta: "event_list"
        composable(Screen.EventList.route) {
            EventListScreen(
                authRepository = authRepository,
                onLogout = {
                    // Navegar a Login cuando el usuario hace logout
                    navController.navigate(Screen.Login.route) {
                        // Limpiar el back stack para que no se pueda volver a EventList
                        popUpTo(Screen.EventList.route) { inclusive = true }
                    }
                }
            )
        }
    }
}