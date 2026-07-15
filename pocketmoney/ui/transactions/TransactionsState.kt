package com.example.pocketmoney.ui.transactions

import com.example.pocketmoney.domain.models.Category
import com.example.pocketmoney.domain.models.TransactionType
import com.example.pocketmoney.domain.models.TransactionWithCategory

data class TransactionsState(
    val transactions: List<TransactionWithCategory> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedType: TransactionType? = null,
    val selectedCategoryId: Long? = null,
    val selectedDateRange: Pair<Long, Long>? = null,
    val currency: String = "RUB",
)