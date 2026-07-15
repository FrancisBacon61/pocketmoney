package com.example.pocketmoney.ui.home

import com.example.pocketmoney.domain.models.Category // ДОБАВИЛИ ИМПОРТ
import com.example.pocketmoney.domain.models.CurrencyRate
import com.example.pocketmoney.domain.models.Transaction
import com.example.pocketmoney.domain.models.TransactionWithCategory

data class HomeState(
    val transactions: List<TransactionWithCategory> = emptyList(),
    val totalBalance: Double = 0.0,
    val currency: String = "RUB",
    val isBalanceHidden: Boolean = false,
    val rates: List<CurrencyRate> = emptyList(),
    val categories: List<Category> = emptyList(),

    val isLoading: Boolean = false,
    val isAddDialogVisible: Boolean = false,
    val transactionToDelete: Transaction? = null
)