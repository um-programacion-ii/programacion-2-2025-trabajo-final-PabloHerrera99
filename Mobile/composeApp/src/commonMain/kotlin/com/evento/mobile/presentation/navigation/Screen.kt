package com.evento.mobile.presentation.navigation

import com.evento.mobile.data.model.purchase.SeatCoordinates
import com.evento.mobile.presentation.screens.assignment.SeatWithName

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

    /**
     * Pantalla de datos de evento.
     * Ruta: "event"
     */
    data object EventDetail : Screen("events/{eventId}") {
        fun createRoute(eventId: Long) = "events/$eventId"
    }

    /**
     * Pantalla de lista de asientos disponibles.
     */
    data object SeatAvailability : Screen("events/{eventId}/seats") {
        fun createRoute(eventId: Long) = "events/$eventId/seats"
    }

    /**
     * Pantalla de asignación de nombres a tickets.
     * Ruta: "sessions/{sessionId}/tickets"
     */
    /**
     * Pantalla de asignación de nombres a tickets.
     * Ruta: "ticket_assignment/{seats}"
     * Parámetro: seats codificados como "1-5,2-3,3-7" (comma-separated)
     */
    data object TicketAssignment : Screen("ticket_assignment/{seats}") {
        fun createRoute(seats: List<SeatCoordinates>): String {
            // Codificar como "1-5,2-3,3-7"
            val encoded = seats.joinToString(",") { "${it.fila}-${it.columna}" }
            return "ticket_assignment/$encoded"
        }
    }

    /**
    * Pantalla de confirmación de compra.
    * Ruta: "purchase_confirmation/{seats}"
    *
    * Parámetro seats codificado como: "1-5-Juan Perez,2-3-Maria Lopez"
    * Formato: "{fila}-{columna}-{nombre}" separados por comas
    */
    data object PurchaseConfirmation : Screen("purchase_confirmation/{seats}") {
        fun createRoute(seats: List<SeatWithName>): String {
            // Codificar asientos como "1-5-Juan,2-3-Maria"
            // Eliminar comas del nombre para evitar conflictos
            val encoded = seats.joinToString(",") { seat ->
                val nombreSanitizado = seat.nombre.replace(",", "").trim()
                "${seat.fila}-${seat.columna}-${nombreSanitizado}"
            }
            return "purchase_confirmation/$encoded"
        }
    }
}