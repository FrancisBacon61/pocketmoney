package com.example.pocketmoney.domain.repository

import com.example.pocketmoney.domain.models.Account
import com.example.pocketmoney.domain.models.Transaction
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun getAllTransactions(): Flow<List<Transaction>>

    // Изменено: получаем данные аккаунта (включая название и баланс)
    fun getAccount(): Flow<Account?>

    suspend fun addTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)

    // НОВОЕ: метод для инициализации или обновления счета (например, изменить название)
    suspend fun updateAccount(account: Account)

    suspend fun clearAllData()
}