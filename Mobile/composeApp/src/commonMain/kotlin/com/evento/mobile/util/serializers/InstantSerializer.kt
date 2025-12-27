package com.evento.mobile.util.serializers

import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
/**
 * Serializador custom para kotlinx.datetime.Instant.
 * Convierte strings ISO-8601 (ej: "2025-12-15T20:00:00Z")
 * hacia/desde objetos Instant de Kotlin.
 * Uso:
 * * @Serializable
 * data class Evento(
 *     @Serializable(with = InstantSerializer::class)
 *     val fecha: Instant
 * )
 *  */
object InstantSerializer : KSerializer<Instant> {

    override val descriptor = PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        return Instant.parse(decoder.decodeString())
    }
}