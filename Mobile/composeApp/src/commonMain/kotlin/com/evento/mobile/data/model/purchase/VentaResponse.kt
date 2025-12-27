package com.evento.mobile.data.model.purchase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
/**
 * DTO de respuesta para una venta confirmada.
 *
 * Mapea desde: VentaDTO.java del backend
 * Endpoint: POST /api/compra/confirmar
 */
@Serializable
data class VentaResponse(
    val id: Long,

    @SerialName("idVentaCatedra")
    val idVentaCatedra: Long? = null,

    @SerialName("fechaVenta")
    val fechaVenta: String,

    @SerialName("precioTotal")
    val precioTotal: Double,
    val exitosa: Boolean,
    val descripcion: String? = null,

    @SerialName("estadoSincronizacion")
    val estadoSincronizacion: String,

    // Evento completo (backend devuelve EventoDTO)
    val evento: EventoMinimalInVenta? = null
)

@Serializable
data class EventoMinimalInVenta(
    val id: Long,
    val titulo: String
)