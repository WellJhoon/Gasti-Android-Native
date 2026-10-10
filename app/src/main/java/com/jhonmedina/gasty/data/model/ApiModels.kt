package com.jhonmedina.gasty.data.model

import com.google.gson.annotations.SerializedName

// =========================================================================
// 📦 WRAPPER GENERAL DE LA API (Equivalente a ApiResponse<T> en TypeScript)
// Express devuelve siempre: { success: true, data: ... }
// =========================================================================
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T,
    @SerializedName("message") val message: String? = null
)

// =========================================================================
// 👤 MODELOS DE AUTENTICACIÓN Y PERFIL
// =========================================================================
data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("color") val color: String? = null,
    @SerializedName("avatar") val avatar: String? = null
)

data class LoginRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("pin") val pin: String
)

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserDto
)

// =========================================================================
// 📊 MODELOS DEL DASHBOARD (Resumen mensual, dinero disponible, etc.)
// =========================================================================
data class DashboardSummaryDto(
    @SerializedName("month") val month: String,
    @SerializedName("income") val income: IncomeSummaryDto,
    @SerializedName("expenses") val expenses: ExpensesSummaryDto,
    @SerializedName("cashAvailability") val cashAvailability: CashAvailabilityDto,
    @SerializedName("netBalance") val netBalance: Double
)

data class IncomeSummaryDto(
    @SerializedName("projected") val projected: Double,
    @SerializedName("actual") val actual: Double,
    @SerializedName("extraThisMonth") val extraThisMonth: Double = 0.0
)

data class ExpensesSummaryDto(
    @SerializedName("actual") val actual: Double,
    @SerializedName("variableThisMonth") val variableThisMonth: Double
)

data class CashAvailabilityDto(
    @SerializedName("availableNow") val availableNow: Double,
    @SerializedName("dailyBudget") val dailyBudget: Double,
    @SerializedName("daysRemainingInMonth") val daysRemainingInMonth: Int,
    @SerializedName("percentageCommitted") val percentageCommitted: Double
)

// =========================================================================
// 💸 MODELOS DE TRANSACCIONES / MOVIMIENTOS
// =========================================================================
data class TransactionDto(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String, // "gasto" o "ingreso"
    @SerializedName("amount") val amount: Double,
    @SerializedName("description") val description: String? = null,
    @SerializedName("date") val date: String,
    @SerializedName("categoryId") val categoryId: String? = null,
    @SerializedName("categoryName") val categoryName: String? = null,
    @SerializedName("categoryIcon") val categoryIcon: String? = null,
    @SerializedName("categoryColor") val categoryColor: String? = null,
    @SerializedName("isShared") val isShared: Boolean = false
)

data class CreateTransactionRequest(
    @SerializedName("type") val type: String, // "gasto" o "ingreso"
    @SerializedName("amount") val amount: Double,
    @SerializedName("categoryId") val categoryId: String,
    @SerializedName("date") val date: String, // "YYYY-MM-DD"
    @SerializedName("description") val description: String? = null,
    @SerializedName("isShared") val isShared: Boolean = false
)

// =========================================================================
// 🏷️ MODELO DE CATEGORÍAS
// =========================================================================
data class CategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("color") val color: String? = null,
    @SerializedName("type") val type: String? = null // "gasto" o "ingreso"
)
