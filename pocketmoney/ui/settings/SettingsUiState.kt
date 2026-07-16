package com.example.pocketmoney.ui.settings

data class SettingsUiState(
    val currency: String = "RUB",
    val isBalanceHidden: Boolean = false,
    val lastCurrencyUpdate: Long = 0L,
    val isRefreshing: Boolean = false,
    val currencyError: CurrencyError? = null
)