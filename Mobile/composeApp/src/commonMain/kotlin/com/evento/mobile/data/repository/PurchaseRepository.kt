package com.evento.mobile.data.repository

import com.evento.mobile.data.model.purchase.*
import com.evento.mobile.data.remote.PurchaseApiService
import com.evento.mobile.util.NetworkResult
/**
 * Repositorio para operaciones de compra.
 *
 * Actúa como capa intermedia entre ViewModels y PurchaseApiService.
 * En esta implementación solo delega las llamadas al ApiService,
 * pero podría agregar lógica adicional (cache, validaciones, etc.)
 */
class PurchaseRepository(private val apiService: PurchaseApiService) {

    suspend fun startSession(eventoId: Long): NetworkResult<SessionResponse> {
        return apiService.startSession(eventoId)
    }

    suspend fun selectSeats(asientos: List<SeatCoordinates>): NetworkResult<SessionResponse> {
        return apiService.selectSeats(asientos)
    }

    suspend fun getSessionState(): NetworkResult<SessionResponse> {
        return apiService.getSessionState()
    }

    suspend fun cancelSession(): NetworkResult<Unit> {
        return apiService.cancelSession()
    }
}