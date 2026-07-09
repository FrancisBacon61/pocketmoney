package com.example.pocketmoney.domain.models

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val date: Long,
    val comment: String,
    val type: TransactionType
)

// НОВОЕ: Сущность счета из ТЗ
data class Account(
    val id: Long = 0,
    val name: String,
    val currentBalance: Double
)

// Тип транзакции
enum class TransactionType {
    INCOME, EXPENSE
}

// модель курса валют
data class CurrencyRate(
    val code: String,
    val rate: Double
)

data class HomeDisplayData(
    val transactions: List<Transaction> = emptyList(),
    val totalBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val currency: String = "RUB",
    val isBalanceHidden: Boolean = false
)