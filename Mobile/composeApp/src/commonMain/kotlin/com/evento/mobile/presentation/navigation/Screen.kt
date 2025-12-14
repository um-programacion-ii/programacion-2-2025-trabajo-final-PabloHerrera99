package com.evento.mobile.presentation.navigation

/**
 * Definición de rutas de navegación de la aplicación.
 *
 * Esta sealed class define todas las pantallas/destinos de navegación
 * disponibles en la app. Cada object representa una ruta única.
 *
 * El uso de sealed class hace imposible navegar a rutas que no existen.
 */
sealed class Screen(val route: String) {

    /**
     * Pantalla de login/autenticación.
     * Ruta: "login"
     */
    data object Login : Screen("login")

    /**
     * Pantalla de lista de eventos.
     * Ruta: "event_list"
     */
    data object EventList : Screen("event_list")
}