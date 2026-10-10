package com.jhonmedina.gasty.data.api

import com.jhonmedina.gasty.data.model.*
import retrofit2.Response
import retrofit2.http.*

// =========================================================================
// 🌐 INTERFAZ DE SERVICIO RETROFIT (Equivalente al cliente Axios/Fetch en TS)
// Retrofit usa anotaciones declarativas (@GET, @POST, etc.)
// y genera la implementación HTTP por nosotros.
// Las funciones 'suspend' son el equivalente nativo a 'async' en TypeScript.
// =========================================================================
interface GastyApiService {

    // --- Perfiles & Autenticación ---
    @GET("auth/profiles")
    suspend fun getProfiles(): Response<ApiResponse<List<UserDto>>>

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<ApiResponse<LoginResponse>>

    @GET("auth/me")
    suspend fun getMe(): Response<ApiResponse<UserDto>>

    // --- Dashboard ---
    @GET("dashboard/summary")
    suspend fun getDashboardSummary(
        @Query("month") month: String? = null
    ): Response<ApiResponse<DashboardSummaryDto>>

    // --- Transacciones (Movimientos) ---
    @GET("transactions")
    suspend fun getTransactions(
        @Query("month") month: String? = null,
        @Query("type") type: String? = null
    ): Response<ApiResponse<List<TransactionDto>>>

    @POST("transactions")
    suspend fun createTransaction(
        @Body body: CreateTransactionRequest
    ): Response<ApiResponse<TransactionDto>>

    // --- Categorías ---
    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<CategoryDto>>>
}
