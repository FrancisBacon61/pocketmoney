package com.example.pocketmoney.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.domain.models.Transaction
import com.example.pocketmoney.domain.models.TransactionType // Добавляем импорт типа транзакции
import com.example.pocketmoney.domain.usecase.GetTransactionsUseCase
import com.example.pocketmoney.data.preferences.SettingsManager
import kotlinx.coroutines.flow.*

class TransactionsViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase,
    // ИСПРАВЛЕНО: Удалили геттер категорий из конструктора
    private val settingsManager: SettingsManager
) : ViewModel() {

    // Вместо ID категории храним выбранный тип транзакции (Доход / Расход / Все)
    private val _selectedType = MutableStateFlow<TransactionType?>(null)
    private val _selectedDateRange = MutableStateFlow<Pair<Long, Long>?>(null)

    @Suppress("UNCHECKED_CAST")
    val uiState: StateFlow<TransactionsState> = combine(
        getTransactionsUseCase(),        // [0] List<Transaction>
        _selectedType,                   // [1] TransactionType?
        _selectedDateRange,              // [2] Pair<Long, Long>?
        settingsManager.currency,        // [3] String
        settingsManager.isBalanceHidden  // [4] Boolean
    ) { flowsArray ->

        val transactions = flowsArray[0] as List<Transaction>
        val selectedType = flowsArray[1] as TransactionType?
        val dateRange = flowsArray[2] as Pair<Long, Long>?
        val currency = flowsArray[3] as String
        val isHidden = flowsArray[4] as Boolean

        // 1. Фильтруем по типу транзакции (Доход/Расход)
        var filtered = if (selectedType == null) {
            transactions
        } else {
            transactions.filter { it.type == selectedType }
        }

        // 2. Фильтруем по дате
        if (dateRange != null) {
            val (startTime, endTime) = dateRange
            filtered = filtered.filter { it.date in startTime..endTime }
        }

        val sortedTransactions = filtered.sortedByDescending { it.date }

        // ИСПРАВЛЕНО: Передаем обновленные параметры в состояние экрана
        TransactionsState(
            transactions = sortedTransactions,
            selectedType = selectedType, // Изменили с selectedCategoryId
            selectedDateRange = dateRange,
            currency = currency,
            isBalanceHidden = isHidden
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionsState()
    )

    // Функция переключения фильтра (Доход / Расход / Показать все)
    fun selectType(type: TransactionType?) {
        _selectedType.value = type
    }

    fun selectDateRange(startTimestamp: Long?, endTimestamp: Long?) {
        if (startTimestamp != null && endTimestamp != null) {
            _selectedDateRange.value = Pair(startTimestamp, endTimestamp)
        } else {
            _selectedDateRange.value = null
        }
    }
}