package com.evento.mobile.data.model.purchase

import kotlinx.serialization.Serializable
/**
 * Estados posibles de una sesión de compra.
 *
 * Mapea desde: EstadoSesion.java (enum) del backend
 */

@Serializable
enum class SessionState {
    SELECCION_ASIENTOS,
    CARGA_DATOS,
    COMPLETADO
}