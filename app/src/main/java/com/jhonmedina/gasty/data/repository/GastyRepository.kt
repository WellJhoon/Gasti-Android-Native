package com.jhonmedina.gasty.data.repository

import com.jhonmedina.gasty.data.api.RetrofitClient
import com.jhonmedina.gasty.data.model.*

// =========================================================================
// 🗄️ REPOSITORIO DE DATOS (Data Repository)
// En TypeScript/React, esto es la capa de servicios ('services/api.ts')
// donde manejas las llamadas HTTP, el desarmado de respuestas y catch de errores.
// =========================================================================
class GastyRepository {

    private val api = RetrofitClient.apiService

    suspend fun getProfiles(): Result<List<UserDto>> = runCatching {
        val response = api.getProfiles()
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()!!.data
        } else {
            throw Exception(response.errorBody()?.string() ?: "Error al obtener perfiles de Gasty")
        }
    }

    suspend fun login(userId: String, pin: String): Result<LoginResponse> = runCatching {
        val response = api.login(LoginRequest(userId = userId, pin = pin))
        if (response.isSuccessful && response.body()?.success == true) {
            val loginData = response.body()!!.data
            // Guardamos el token en memoria para que las llamadas subsecuentes tengan Bearer Token
            RetrofitClient.setAuthToken(loginData.token)
            loginData
        } else {
            throw Exception(response.errorBody()?.string() ?: "PIN incorrecto o error de autenticación")
        }
    }

    suspend fun getDashboardSummary(month: String? = null): Result<DashboardSummaryDto> = runCatching {
        val response = api.getDashboardSummary(month)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()!!.data
        } else {
            throw Exception(response.errorBody()?.string() ?: "Error al cargar métricas del dashboard")
        }
    }

    suspend fun getTransactions(month: String? = null, type: String? = null): Result<List<TransactionDto>> = runCatching {
        val response = api.getTransactions(month, type)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()!!.data
        } else {
            throw Exception(response.errorBody()?.string() ?: "Error al cargar movimientos")
        }
    }

    suspend fun createTransaction(request: CreateTransactionRequest): Result<TransactionDto> = runCatching {
        val response = api.createTransaction(request)
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()!!.data
        } else {
            throw Exception(response.errorBody()?.string() ?: "Error al registrar la transacción")
        }
    }

    suspend fun getCategories(): Result<List<CategoryDto>> = runCatching {
        val response = api.getCategories()
        if (response.isSuccessful && response.body()?.success == true) {
            response.body()!!.data
        } else {
            throw Exception(response.errorBody()?.string() ?: "Error al cargar categorías")
        }
    }
}
