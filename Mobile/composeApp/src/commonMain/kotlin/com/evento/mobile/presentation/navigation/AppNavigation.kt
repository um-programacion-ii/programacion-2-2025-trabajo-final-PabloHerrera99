package com.evento.mobile.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.evento.mobile.data.remote.AuthApiService
import com.evento.mobile.data.remote.EventApiService
import com.evento.mobile.data.repository.AuthRepository
import com.evento.mobile.data.repository.EventRepository
import com.evento.mobile.di.HttpClientProvider
import com.evento.mobile.presentation.screens.events.EventListScreen
import com.evento.mobile.presentation.screens.events.EventListViewModel
import com.evento.mobile.presentation.screens.login.LoginScreen
import com.evento.mobile.presentation.screens.login.LoginViewModel
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    // Inicialización de dependencias (DI manual)
    // 1. Crear HttpClient configurado
    val httpClient = HttpClientProvider.create()

    // 2. Crear AuthApiService con el HttpClient
    val authApiService = AuthApiService(httpClient)

    // 3. Crear AuthRepository con el AuthApiService
    val authRepository = AuthRepository(authApiService)

    // 4. Crear EventApiService con el HttpClient
    val eventApiService = EventApiService(httpClient)

    // 5. Crear EventRepository con el EventApiService
    val eventRepository = EventRepository(eventApiService)

    // 6. Configurar el proveedor de token para HttpClient
    HttpClientProvider.setTokenProvider { authRepository.getToken() }

    // 7. Crear LoginViewModel con el AuthRepository
    val loginViewModel = LoginViewModel(authRepository)
    // Configuración de navegación
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        // Ruta: "login"
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.EventList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        // Ruta: "event_list"
        composable(Screen.EventList.route) {
            val eventListViewModel = EventListViewModel(eventRepository, authRepository)

            EventListScreen(
                viewModel = eventListViewModel,
                onEventClick = { eventId ->
                    // TODO: Navegar a detalle (próximo sprint)
                    println("Click en evento: $eventId")
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.EventList.route) { inclusive = true }
                    }
                }
            )
        }
    }
}