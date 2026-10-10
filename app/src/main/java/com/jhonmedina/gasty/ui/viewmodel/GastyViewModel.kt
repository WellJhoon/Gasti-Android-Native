package com.jhonmedina.gasty.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jhonmedina.gasty.data.api.RetrofitClient
import com.jhonmedina.gasty.data.model.*
import com.jhonmedina.gasty.data.repository.GastyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// =========================================================================
// 🧠 ESTADO GLOBAL DE LA UI (GastyUiState)
// En React / Zustand esto es tu Store:
// interface GastyState {
//   isLoading: boolean;
//   isConnectedToApi: boolean;
//   activeUser: User | null;
//   dashboardSummary: DashboardSummary | null;
//   transactions: Transaction[];
// }
// =========================================================================
data class GastyUiState(
    val isLoading: Boolean = false,
    val isConnectedToApi: Boolean = false,
    val errorMessage: String? = null,
    val activeUser: UserDto? = null,
    val availableProfiles: List<UserDto> = emptyList(),
    val dashboardSummary: DashboardSummaryDto? = null,
    val transactions: List<TransactionDto> = emptyList(),
    val categories: List<CategoryDto> = emptyList()
)

// =========================================================================
// ⚡ VIEWMODEL: Lógica de negocio y puente entre Repositorio y Compose UI
// En React, esto combina tu Custom Hook + Zustand Action Dispatchers + useEffect
// =========================================================================
class GastyViewModel(
    private val repository: GastyRepository = GastyRepository()
) : ViewModel() {

    // Privado y mutable (solo el ViewModel puede cambiarlo)
    private val _uiState = MutableStateFlow(GastyUiState())
    // Público e inmutable (la UI solo puede leerlo y reaccionar a cambios)
    val uiState: StateFlow<GastyUiState> = _uiState.asStateFlow()

    init {
        // Al crearse el ViewModel (equivalente a useEffect([], []) al montar la app),
        // consultamos los perfiles y el estado de la API
        refreshData()
    }

    /**
     * Refresca todos los datos desde el backend Express en segundo plano.
     * viewModelScope.launch es como un bloque async que se cancela automáticamente
     * si el componente o pantalla se destruye, evitando memory leaks.
     */
    fun refreshData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 1. Cargar perfiles disponibles
            repository.getProfiles().fold(
                onSuccess = { profiles ->
                    _uiState.update {
                        it.copy(
                            availableProfiles = profiles,
                            activeUser = it.activeUser ?: profiles.firstOrNull(),
                            isConnectedToApi = true
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isConnectedToApi = false,
                            errorMessage = error.localizedMessage
                        )
                    }
                }
            )

            // 2. Si ya tenemos token o si podemos consultar, traemos dashboard y transacciones
            repository.getDashboardSummary().onSuccess { summary ->
                _uiState.update { it.copy(dashboardSummary = summary) }
            }

            repository.getTransactions().onSuccess { txs ->
                _uiState.update { it.copy(transactions = txs) }
            }

            repository.getCategories().onSuccess { cats ->
                _uiState.update { it.copy(categories = cats) }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Autentica con PIN al usuario seleccionado (Login)
     */
    fun login(userId: String, pin: String, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.login(userId, pin).fold(
                onSuccess = { loginResponse ->
                    _uiState.update {
                        it.copy(
                            activeUser = loginResponse.user,
                            isConnectedToApi = true,
                            isLoading = false
                        )
                    }
                    // Una vez autenticado, traemos todos los datos frescos protegidos
                    refreshData()
                    onComplete(true)
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            errorMessage = error.localizedMessage ?: "Error de autenticación",
                            isLoading = false
                        )
                    }
                    onComplete(false)
                }
            )
        }
    }

    /**
     * Cierra la sesión activa
     */
    fun logout() {
        RetrofitClient.setAuthToken(null)
        _uiState.update {
            it.copy(
                activeUser = null,
                dashboardSummary = null,
                transactions = emptyList()
            )
        }
    }

    /**
     * Registra un nuevo gasto o ingreso
     */
    fun addTransaction(
        type: String, // "gasto" o "ingreso"
        amount: Double,
        categoryId: String,
        date: String,
        description: String? = null,
        isShared: Boolean = false,
        onComplete: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {
            val req = CreateTransactionRequest(
                type = type,
                amount = amount,
                categoryId = categoryId,
                date = date,
                description = description,
                isShared = isShared
            )
            repository.createTransaction(req).fold(
                onSuccess = {
                    refreshData() // Refresca balance y transacciones
                    onComplete(true)
                },
                onFailure = {
                    onComplete(false)
                }
            )
        }
    }
}
