package com.evento.mobile.data.model.purchase

import kotlinx.serialization.Serializable
/**
 * DTO de request para asignar nombres a los asientos seleccionados.
 *
 * Endpoint: POST /api/compra/asignar-nombres
 *
 * Formato del Map:
 * - Key: "fila-columna" (ejemplo: "1-5")
 * - Value: Nombre completo de la persona (ejemplo: "Juan Pérez")
 */
@Serializable
data class AssignNamesRequest(
    val nombres: Map<String, String>
)