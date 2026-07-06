package com.example.pocketmoney.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
// ИСПРАВЛЕНО: Добавляем недостающие импорты доменных моделей
import com.example.pocketmoney.domain.models.Transaction
import com.example.pocketmoney.domain.models.Category
// Импорты интеракторов (UseCases) и менеджера настроек
import com.example.pocketmoney.domain.usecase.GetCategoriesUseCase
import com.example.pocketmoney.domain.usecase.GetTransactionsUseCase
import com.example.pocketmoney.data.preferences.SettingsManager
import kotlinx.coroutines.flow.*

class TransactionsViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    private val _selectedDateRange = MutableStateFlow<Pair<Long, Long>?>(null)
    @Suppress("UNCHECKED_CAST")
    val uiState: StateFlow<TransactionsState> = combine(
        getTransactionsUseCase(),        // [0] List<Transaction>
        _selectedCategoryId,             // [1] Long?
        _selectedDateRange,              // [2] Pair<Long, Long>?
        settingsManager.currency,        // [3] String
        settingsManager.isBalanceHidden, // [4] Boolean
        getCategoriesUseCase()           // [5] List<Category>
    ) { flowsArray ->
        // Подавляем варнинг только для этих строчек

        val transactions = flowsArray[0] as List<Transaction>
        val selectedId = flowsArray[1] as Long?
        val dateRange = flowsArray[2] as Pair<Long, Long>?
        val currency = flowsArray[3] as String
        val isHidden = flowsArray[4] as Boolean
        val categories = flowsArray[5] as List<Category>

        // 1. Фильтруем по категории
        var filtered = if (selectedId == null) {
            transactions
        } else {
            transactions.filter { it.categoryId == selectedId }
        }

        // 2. Фильтруем по дате
        if (dateRange != null) {
            val (startTime, endTime) = dateRange
            filtered = filtered.filter { it.date in startTime..endTime }
        }

        val sortedTransactions = filtered.sortedByDescending { it.date }

        TransactionsState(
            transactions = sortedTransactions,
            categories = categories,
            selectedCategoryId = selectedId,
            selectedDateRange = dateRange,
            currency = currency,
            isBalanceHidden = isHidden
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionsState()
    )

    fun selectCategory(id: Long?) {
        _selectedCategoryId.value = id
    }

    fun selectDateRange(startTimestamp: Long?, endTimestamp: Long?) {
        if (startTimestamp != null && endTimestamp != null) {
            _selectedDateRange.value = Pair(startTimestamp, endTimestamp)
        } else {
            _selectedDateRange.value = null
        }
    }
}