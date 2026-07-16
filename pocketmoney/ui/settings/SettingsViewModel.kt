package com.example.pocketmoney.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocketmoney.data.preferences.SettingsManager
import com.example.pocketmoney.domain.repository.FinanceRepository
import com.example.pocketmoney.domain.usecase.RefreshCurrencyRatesUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsManager: SettingsManager,
    private val financeRepository: FinanceRepository,
    private val refreshCurrencyRatesUseCase: RefreshCurrencyRatesUseCase
) : ViewModel() {

    private val _isRefreshing = MutableStateFlow(false)

    private val _currencyError = MutableStateFlow<CurrencyError?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsManager.currency,
        settingsManager.isBalanceHidden,
        settingsManager.lastCurrencyUpdate,
        _isRefreshing,
        _currencyError
    ) { currency, isBalanceHidden, lastUpdate, isRefreshing, error ->
        SettingsUiState(
            currency = currency,
            isBalanceHidden = isBalanceHidden,
            lastCurrencyUpdate = lastUpdate,
            isRefreshing = isRefreshing,
            currencyError = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setCurrency(code: String) {
        viewModelScope.launch {
            settingsManager.setCurrency(code)
            refreshRates(forceRefresh = false)
        }
    }

    fun toggleBalanceHidden(hidden: Boolean) {
        viewModelScope.launch {
            settingsManager.setBalanceHidden(hidden)
        }
    }

    fun refreshRates(forceRefresh: Boolean = true) {
        viewModelScope.launch {
            _isRefreshing.value = true
            _currencyError.value = null

            try {
                refreshCurrencyRatesUseCase(forceRefresh = forceRefresh)
            } catch (e: java.io.IOException) {
                e.printStackTrace()
                _currencyError.value = CurrencyError.NO_INTERNET
            } catch (e: Exception) {
                e.printStackTrace()
                _currencyError.value = CurrencyError.UNKNOWN
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            financeRepository.clearAllData()
        }
    }
}