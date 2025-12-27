package com.evento.mobile.presentation.navigation

import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.evento.mobile.data.remote.AuthApiService
import com.evento.mobile.data.remote.EventApiService
import com.evento.mobile.data.remote.PurchaseApiService
import com.evento.mobile.data.repository.AuthRepository
import com.evento.mobile.data.repository.EventRepository
import com.evento.mobile.data.repository.PurchaseRepository
import com.evento.mobile.data.model.purchase.SeatCoordinates
import com.evento.mobile.di.HttpClientProvider
import com.evento.mobile.presentation.screens.assignment.SeatWithName
import com.evento.mobile.presentation.screens.events.EventListScreen
import com.evento.mobile.presentation.screens.events.EventListViewModel
import com.evento.mobile.presentation.screens.login.LoginScreen
import com.evento.mobile.presentation.screens.login.LoginViewModel
import com.evento.mobile.presentation.screens.detail.EventDetailScreen
import com.evento.mobile.presentation.screens.detail.EventDetailViewModel
import com.evento.mobile.presentation.screens.seats.SeatSelectionScreen
import com.evento.mobile.presentation.screens.seats.SeatSelectionViewModel
import com.evento.mobile.presentation.screens.assignment.TicketAssignmentScreen
import com.evento.mobile.presentation.screens.assignment.TicketAssignmentViewModel
import com.evento.mobile.presentation.screens.confirmation.PurchaseConfirmationScreen
import com.evento.mobile.presentation.screens.confirmation.PurchaseConfirmationViewModel

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

    // 6. Crear PurchaseApiService con el HttpClient
    val purchaseApiService = PurchaseApiService(httpClient)

    // 7. Crear PurchaseRepository con el PurchaseApiService
    val purchaseRepository = PurchaseRepository(purchaseApiService)

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
                    navController.navigate(Screen.EventDetail.createRoute(eventId))
                },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.EventList.route) { inclusive = true }
                    }
                }
            )
        }

        // Event Detail
        composable(
            route = Screen.EventDetail.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            // Extraer el parámetro eventId de la ruta
            val eventId = backStackEntry.arguments?.getLong("eventId") ?: 0L

            // Crear el ViewModel
            val viewModel = remember { EventDetailViewModel(eventRepository) }

            // Mostrar la pantalla
            EventDetailScreen(
                eventId = eventId,
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSeats = { eventId ->
                    navController.navigate(Screen.SeatAvailability.createRoute(eventId))
                }
            )
        }

        // Seat Selection
        composable(
            route = Screen.SeatAvailability.route,
            arguments = listOf(
                navArgument("eventId") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getLong("eventId") ?: 0L

            val viewModel = remember {
                SeatSelectionViewModel(
                    eventRepository = eventRepository,
                    purchaseRepository = purchaseRepository,
                    eventoId = eventId
                )
            }

            SeatSelectionScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToTicketAssignment = { selectedSeats ->
                    navController.navigate(Screen.TicketAssignment.createRoute(selectedSeats))
                }
            )
        }

        // Ticket Assignment
        composable(
            route = "ticket_assignment/{seats}",
            arguments = listOf(
                navArgument("seats") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val seatsEncoded = backStackEntry.arguments?.getString("seats") ?: ""

            // Decodificar "1-5,2-3,3-7" -> List<SeatCoordinates>
            val selectedSeats = seatsEncoded.split(",").mapNotNull { seatStr ->
                val parts = seatStr.split("-")
                if (parts.size == 2) {
                    SeatCoordinates(
                        fila = parts[0].toIntOrNull() ?: 0,
                        columna = parts[1].toIntOrNull() ?: 0
                    )
                } else null
            }

            val viewModel = remember {
                TicketAssignmentViewModel(
                    purchaseRepository = purchaseRepository,
                    selectedSeats = selectedSeats
                )
            }

            TicketAssignmentScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onContinue = { seats ->  // ← Recibir asientos
                    navController.navigate(
                        Screen.PurchaseConfirmation.createRoute(seats)  // ← Usar createRoute
                    ) {
                        popUpTo(Screen.EventList.route)
                    }
                }
            )
        }

        // Purchase Confirmation
        composable(
            route = Screen.PurchaseConfirmation.route,
            arguments = listOf(
                navArgument("seats") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val seatsEncoded = backStackEntry.arguments?.getString("seats") ?: ""

            // Decodificar "1-5-Juan,2-3-Maria" -> List<SeatWithName>
            val selectedSeats = seatsEncoded.split(",").mapNotNull { seatStr ->
                val parts = seatStr.split("-")
                if (parts.size >= 3) {
                    SeatWithName(
                        fila = parts[0].toIntOrNull() ?: 0,
                        columna = parts[1].toIntOrNull() ?: 0,
                        nombre = parts.drop(2).joinToString("-") // Por si el nombre tenía guiones
                    )
                } else null
            }

            val viewModel = remember {
                PurchaseConfirmationViewModel(
                    purchaseRepository = purchaseRepository,
                    selectedSeats = selectedSeats
                )
            }

            PurchaseConfirmationScreen(
                viewModel = viewModel,
                onNavigateToEventList = {
                    navController.navigate(Screen.EventList.route) {
                        popUpTo(Screen.EventList.route) { inclusive = true }
                    }
                }
            )
        }
    }
}