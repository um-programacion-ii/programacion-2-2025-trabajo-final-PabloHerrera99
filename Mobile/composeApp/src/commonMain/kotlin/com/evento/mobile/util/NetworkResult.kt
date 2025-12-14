package com.evento.mobile.util

/**
 * Wrapper genérico para resultados de operaciones de red.
 *
 * Representa los tres estados posibles de una petición HTTP:
 * - Loading: Operación en progreso
 * - Success: Operación exitosa con datos
 * - Error: Operación fallida con mensaje de error
 */
sealed class NetworkResult<out T> {

    /**
     * Operación exitosa con datos del tipo T
     * @param data Datos retornados por la operación
     */
    data class Success<T>(val data: T) : NetworkResult<T>()

    /**
     * Error con mensaje descriptivo y código HTTP opcional
     * @param message Mensaje de error legible para el usuario
     * @param code Código HTTP de error (opcional)
     */
    data class Error(
        val message: String,
        val code: Int? = null
    ) : NetworkResult<Nothing>()

    /**
     * Operación en progreso
     */
    data object Loading : NetworkResult<Nothing>()
}