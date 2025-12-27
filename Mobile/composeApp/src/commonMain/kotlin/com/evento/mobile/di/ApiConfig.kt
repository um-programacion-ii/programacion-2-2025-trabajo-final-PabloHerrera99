package com.evento.mobile.di

/**
 * URL base del backend.
 *
 * Implementación específica por plataforma usando expect/actual:
 * - Android: http://10.0.2.2:8081 (emulador apunta a localhost del host)
 * - iOS: http://localhost:8081 (simulador usa localhost directamente)
 */
expect object ApiConfig {
    val BASE_URL: String
}
/**
 * Definición de todos los endpoints del API
 */
object Endpoints {
    // Autenticación
    const val AUTHENTICATE = "/api/authenticate"

    // Eventos
    const val EVENTOS = "/api/eventos"
    const val ASIENTOS_DISPONIBILIDAD = "/api/eventos/{id}/asientos/disponibilidad"

    // Proceso de compra
    const val COMPRA_INICIAR = "/api/compra/iniciar"
    const val COMPRA_SELECCIONAR_ASIENTOS = "/api/compra/seleccionar-asientos"
    const val COMPRA_ASIGNAR_NOMBRES = "/api/compra/asignar-nombres"
    const val COMPRA_CONFIRMAR = "/api/compra/confirmar"
    const val COMPRA_CANCELAR = "/api/compra/cancelar"
    const val COMPRA_ESTADO = "/api/compra/estado"

    // Ventas
    const val VENTAS = "/api/ventas"
    const val ASIENTOS_VENDIDOS = "/api/asiento-vendidos"
}