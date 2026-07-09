package com.example.pocketmoney.ui.transactions

import com.example.pocketmoney.domain.models.Transaction
import com.example.pocketmoney.domain.models.TransactionType

data class TransactionsState(
    val transactions: List<Transaction> = emptyList(),
    val selectedType: TransactionType? = null,
    val selectedDateRange: Pair<Long, Long>? = null, // <-- ДОБАВЬ ЭТУ СТРОЧКУ
    val currency: String = "RUB",
    val isBalanceHidden: Boolean = false
)