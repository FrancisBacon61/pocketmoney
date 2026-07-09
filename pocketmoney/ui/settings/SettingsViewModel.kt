package com.example.pocketmoney.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.data.preferences.SettingsManager
import com.example.pocketmoney.domain.repository.FinanceRepository
import com.example.pocketmoney.domain.usecase.RefreshCurrencyRatesUseCase // Импортируем юзкейс
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsManager: SettingsManager,
    private val financeRepository: FinanceRepository,
    private val refreshCurrencyRatesUseCase: RefreshCurrencyRatesUseCase // ИСПРАВЛЕНО: Теперь тут UseCase!
) : ViewModel() {

    val currency = settingsManager.currency.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "RUB")
    val isBalanceHidden = settingsManager.isBalanceHidden.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val lastCurrencyUpdate = settingsManager.lastCurrencyUpdate.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    fun setCurrency(code: String) {
        viewModelScope.launch {
            settingsManager.setCurrency(code)
            refreshRates(forceRefresh = false)
        }
    }

    fun toggleBalanceHidden(hidden: Boolean) {
        viewModelScope.launch { settingsManager.setBalanceHidden(hidden) }
    }

    fun refreshRates(forceRefresh: Boolean = true) {
        viewModelScope.launch {
            _isRefreshing.value = true
            refreshCurrencyRatesUseCase(forceRefresh = forceRefresh)
            _isRefreshing.value = false
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            financeRepository.clearAllData()
        }
    }
}