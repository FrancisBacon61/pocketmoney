package com.example.pocketmoney.domain.repository

import com.example.pocketmoney.domain.models.Account
import com.example.pocketmoney.domain.models.Category
import com.example.pocketmoney.domain.models.Transaction
import com.example.pocketmoney.domain.models.TransactionType
import com.example.pocketmoney.domain.models.TransactionWithCategory
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun getRecentTransactions(): Flow<List<TransactionWithCategory>>

    fun getAccount(): Flow<Account?>

    suspend fun addTransaction(transaction: Transaction)
    suspend fun deleteTransaction(transaction: Transaction)
    suspend fun updateAccount(account: Account)
    suspend fun clearAllData()

    fun getFilteredTransactions(
        type: TransactionType?,
        categoryId: Long?,
        startDate: Long?,
        endDate: Long?
    ): Flow<List<TransactionWithCategory>>

    fun getAllCategories(): Flow<List<Category>>
    suspend fun insertCategory(category: Category): Long
    suspend fun getDefaultCategory(): Category?
}