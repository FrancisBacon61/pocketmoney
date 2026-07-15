package com.example.pocketmoney.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.data.preferences.SettingsManager
import com.example.pocketmoney.domain.models.Category
import com.example.pocketmoney.domain.models.TransactionType
import com.example.pocketmoney.domain.usecase.GetAllCategoriesUseCase
import com.example.pocketmoney.domain.usecase.GetFilteredTransactionsUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalCoroutinesApi::class)
class TransactionsViewModel(
    private val getFilteredTransactionsUseCase: GetFilteredTransactionsUseCase,
    getAllCategoriesUseCase: GetAllCategoriesUseCase,
    settingsManager: SettingsManager
) : ViewModel() {

    private val _selectedType = MutableStateFlow<TransactionType?>(null)
    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    private val _selectedDateRange = MutableStateFlow<Pair<Long, Long>?>(null)

    private val filtersFlow = combine(
        _selectedType,
        _selectedCategoryId,
        _selectedDateRange
    ) { type, categoryId, dateRange ->
        Filters(type, categoryId, dateRange)
    }

    val uiState: StateFlow<TransactionsState> = combine(
        filtersFlow,
        settingsManager.currency,
        getAllCategoriesUseCase().onStart { emit(emptyList()) }
    ) { filters, currency, categories ->
        CombinedParams(filters, currency, categories)
    }.flatMapLatest { params ->
        getFilteredTransactionsUseCase(
            type = params.filters.type,
            categoryId = params.filters.categoryId,
            dateRange = params.filters.dateRange
        ).map { transactions ->
            TransactionsState(
                transactions = transactions,
                categories = params.categories,
                selectedType = params.filters.type,
                selectedCategoryId = params.filters.categoryId,
                selectedDateRange = params.filters.dateRange,
                currency = params.currency
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TransactionsState()
    )

    fun selectType(type: TransactionType?) { _selectedType.value = type }
    fun selectCategory(categoryId: Long?) { _selectedCategoryId.value = categoryId }
    fun selectDateRange(startTimestamp: Long?, endTimestamp: Long?) {
        _selectedDateRange.value = if (startTimestamp != null && endTimestamp != null) {
            Pair(startTimestamp, endTimestamp)
        } else {
            null
        }
    }
}

private data class Filters(
    val type: TransactionType?,
    val categoryId: Long?,
    val dateRange: Pair<Long, Long>?
)

private data class CombinedParams(
    val filters: Filters,
    val currency: String,
    val categories: List<Category>
)