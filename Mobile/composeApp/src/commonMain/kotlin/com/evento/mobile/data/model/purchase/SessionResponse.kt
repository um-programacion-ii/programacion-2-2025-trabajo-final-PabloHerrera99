package com.evento.mobile.data.model.purchase

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * DTO que representa el estado de una sesión de compra.
 *
 * Mapea desde: SesionDTO.java del backend
 *
 * El backend devuelve objetos anidados completos (evento, usuario),
 * pero en el front los simplificamos a IDs directos para facilitar el uso.
 *
 * Campos del backend (SesionDTO.java):
 * - id: Long
 * - estado: EstadoSesion (ACTIVO, COMPLETADO)
 * - fechaInicio: Instant
 * - ultimaActividad: Instant
 * - expiracion: Instant
 * - activa: Boolean
 * - usuario: UserDTO (objeto anidado completo)
 * - evento: EventoDTO (objeto anidado completo)
 *
 * Este DTO mapea esos campos a una estructura más simple:
 * - id: String (convertido de Long para consistencia)
 * - eventoId: Long (extraído de evento.id)
 * - estado: SessionState
 * - fecha_creacion: String (mapeado de fechaInicio)
 * - fecha_expiracion: String (mapeado de expiracion)
 */
@Serializable
data class SessionResponse(
    // Backend: id (Long) → Front: id (String convertido)
    @SerialName("id")
    private val _id: Long,

    // Backend: estado (EstadoSesion) → Front: estado (SessionState)
    val estado: SessionState,

    // Backend: fechaInicio (Instant/String) → Front: fecha_creacion
    @SerialName("fechaInicio")
    val fecha_creacion: String,

    // Backend: expiracion (Instant/String) → Front: fecha_expiracion
    @SerialName("expiracion")
    val fecha_expiracion: String,

    // Backend: ultimaActividad - recibimos pero no exponemos públicamente
    @SerialName("ultimaActividad")
    private val _ultimaActividad: String? = null,

    // Backend: activa - recibimos pero no exponemos públicamente
    @SerialName("activa")
    private val _activa: Boolean? = null,

    // Backend: usuario (UserDTO completo) - solo para deserialización
    @SerialName("usuario")
    private val _usuario: UsuarioMinimalResponse,

    // Backend: evento (EventoDTO completo) - extraemos el ID
    @SerialName("evento")
    private val _evento: EventoMinimalResponse,

    // Asientos seleccionados - campo opcional
    // Nota: El SesionDTO base del backend NO incluye este campo,
    // pero lo mantenemos nullable para compatibilidad futura
    @SerialName("asientos_seleccionados")
    val asientosSeleccionados: List<SeatCoordinates>? = null
) {
    /**
     * ID de la sesión como String.
     *
     * El backend devuelve Long, pero lo convertimos a String
     * para mantener consistencia con el resto de la app.
     */
    val id: String
        get() = _id.toString()

    /**
     * ID del evento extraído del objeto evento anidado.
     *
     * Permite acceder directamente a eventoId sin navegar
     * por la estructura anidada.
     */
    val eventoId: Long
        get() = _evento.id

    /**
     * Alias para fecha_creacion (compatibilidad con snake_case y camelCase)
     */
    val fechaCreacion: String
        get() = fecha_creacion

    /**
     * Alias para fecha_expiracion (compatibilidad con snake_case y camelCase)
     */
    val fechaExpiracion: String
        get() = fecha_expiracion
}