package com.example.pocketmoney.domain.models

data class Transaction(
    val id: Long = 0,
    val amount: Double,
    val categoryId: Long,
    val date: Long,
    val comment: String,
    val type: TransactionType
)

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
data class Category(
    val id: Long = 0,
    val name: String,
    val icon: String,     // Иконка или эмодзи
    val colorHex: String  // HEX-код цвета
)
data class TransactionWithCategory(
    val transaction: Transaction,
    val category: Category? // Сюда прилетит категория, если она есть (иначе дефолтная)
)
data class HomeDisplayData(
    val transactions: List<TransactionWithCategory> = emptyList(),
    val totalBalance: Double = 0.0,
    val currency: String = "RUB",
    val isBalanceHidden: Boolean = false
)