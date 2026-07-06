package com.example.pocketmoney.ui.transactions

import com.example.pocketmoney.domain.models.Category
import com.example.pocketmoney.domain.models.Transaction

data class TransactionsState(
    val transactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Long? = null,
    val selectedDateRange: Pair<Long, Long>? = null, // <-- ДОБАВЬ ЭТУ СТРОЧКУ
    val currency: String = "RUB",
    val isBalanceHidden: Boolean = false
)