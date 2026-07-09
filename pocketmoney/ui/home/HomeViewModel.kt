package com.example.pocketmoney.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.data.preferences.SettingsManager
import com.example.pocketmoney.domain.models.Transaction
import com.example.pocketmoney.domain.models.TransactionType
import com.example.pocketmoney.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    getHomeDisplayDataUseCase: GetHomeDisplayDataUseCase,
    getCurrencyRatesUseCase: GetCurrencyRatesUseCase, // Оставляем для передачи сырых rates в стейт, если нужно
    private val addTransactionUseCase: AddTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val refreshCurrencyRatesUseCase: RefreshCurrencyRatesUseCase,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _internalState = MutableStateFlow(HomeDialogState())
    val dialogState: StateFlow<HomeDialogState> = _internalState.asStateFlow()

    // Соединяем готовые расчеты из UseCase и текущие курсы валют для UI
    // Соединяем готовые расчеты из UseCase и текущие курсы валют для UI
    val uiState: StateFlow<HomeUiState> = combine(
        getHomeDisplayDataUseCase(),
        getCurrencyRatesUseCase().onStart { emit(emptyList()) }
    ) { domainData, currentRates ->
        HomeUiState.Content(
            state = HomeState(
                transactions = domainData.transactions,
                totalBalance = domainData.totalBalance,
                totalIncome = domainData.totalIncome,
                totalExpenses = domainData.totalExpenses,
                currency = domainData.currency,
                isBalanceHidden = domainData.isBalanceHidden,
                rates = currentRates,
                isLoading = false
            )
        ) as HomeUiState
    }.catch { emit(HomeUiState.Error(it.localizedMessage ?: "Unknown Error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState.Loading
        )

    init {
        viewModelScope.launch {
            try {
                refreshCurrencyRatesUseCase()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addTransaction(amount: Double, comment: String, isIncome: Boolean) {
        viewModelScope.launch {
            val currentState = uiState.value
            if (currentState is HomeUiState.Content) {
                val currentCurrency = currentState.state.currency
                val newTx = Transaction(
                    amount = amount,
                    date = System.currentTimeMillis(),
                    comment = comment,
                    type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE
                )
                addTransactionUseCase(newTx, currentCurrency)
                hideAddDialog()
            }
        }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            deleteTransactionUseCase(transaction)
            dismissDeleteDialog()
        }
    }

    fun toggleBalanceVisibility() {
        viewModelScope.launch {
            val currentState = uiState.value
            if (currentState is HomeUiState.Content) {
                settingsManager.setBalanceHidden(!currentState.state.isBalanceHidden)
            }
        }
    }

    fun confirmDeleteTransaction(transaction: Transaction) { _internalState.update { it.copy(transactionToDelete = transaction) } }
    fun dismissDeleteDialog() { _internalState.update { it.copy(transactionToDelete = null) } }
    fun showAddDialog() { _internalState.update { it.copy(isAddDialogVisible = true) } }
    fun hideAddDialog() { _internalState.update { it.copy(isAddDialogVisible = false) } }
}

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Content(val state: HomeState) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

data class HomeDialogState(
    val isAddDialogVisible: Boolean = false,
    val transactionToDelete: Transaction? = null
)