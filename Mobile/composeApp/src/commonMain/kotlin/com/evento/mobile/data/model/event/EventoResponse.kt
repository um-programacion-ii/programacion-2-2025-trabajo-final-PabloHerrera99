package com.evento.mobile.data.model.event

import com.evento.mobile.util.serializers.InstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
/**
 * DTO que representa un evento completo.
 *
 * Mapea desde: EventoDTO.java del backend
 */
@Serializable
data class EventoResponse(
    val id: Long,
    val idCatedra: Long? = null,
    val titulo: String,
    val resumen: String? = null,
    val descripcion: String? = null,

    @Serializable(with = InstantSerializer::class)
    val fecha: Instant,

    val direccion: String? = null,
    val imagen: String? = null,
    val filaAsientos: Int? = null,
    val columnaAsientos: Int? = null,

    // Backend usa BigDecimal, en KMP usamos Double
    val precioEntrada: Double,

    val activo: Boolean? = null,

    @Serializable(with = InstantSerializer::class)
    val fechaSincronizacion: Instant? = null,

    val eventoTipo: EventoTipoResponse
) {
    /**
     * Propiedad computada: Total de asientos del evento.
     */
    val totalAsientos: Int
        get() = (filaAsientos ?: 0) * (columnaAsientos ?: 0)
}