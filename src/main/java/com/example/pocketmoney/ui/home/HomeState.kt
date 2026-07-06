package com.example.pocketmoney.ui.home

import com.example.pocketmoney.domain.models.CurrencyRate
import com.example.pocketmoney.domain.models.Transaction

data class HomeState(
    val transactions: List<Transaction> = emptyList(),
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,    // ДОБАВИЛИ ДЛЯ ЭКРАНА
    val totalExpenses: Double = 0.0,  // ДОБАВИЛИ ДЛЯ ЭКРАНА
    val currency: String = "RUB",
    val isBalanceHidden: Boolean = false,
    val rates: List<CurrencyRate> = emptyList(),
    val isLoading: Boolean = false,
    val isAddDialogVisible: Boolean = false,
    val transactionToDelete: Transaction? = null
)