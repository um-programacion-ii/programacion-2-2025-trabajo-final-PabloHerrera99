package com.evento.mobile.util

import kotlinx.datetime.LocalDateTime

/**
 * Formatea una fecha en formato corto: "15/12/2025 20:00"
 *
 * @param dateTime Fecha y hora a formatear
 * @return String formateado en formato DD/MM/YYYY HH:mm
 */
fun formatFechaCorta(dateTime: LocalDateTime): String {
    val dia = dateTime.dayOfMonth.toString().padStart(2, '0')
    val mes = dateTime.monthNumber.toString().padStart(2, '0')
    val año = dateTime.year
    val hora = dateTime.hour.toString().padStart(2, '0')
    val minuto = dateTime.minute.toString().padStart(2, '0')

    return "$dia/$mes/$año $hora:$minuto"
}