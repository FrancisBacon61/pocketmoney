package com.example.pocketmoney.data.repository

import com.example.pocketmoney.data.local.FinanceDao
import com.example.pocketmoney.data.local.toDomain
import com.example.pocketmoney.data.local.toEntity
import com.example.pocketmoney.domain.repository.FinanceRepository
import com.example.pocketmoney.domain.models.Account
import com.example.pocketmoney.domain.models.Category
import com.example.pocketmoney.domain.models.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FinanceRepositoryImpl(
    private val dao: FinanceDao // Внедряется через Koin
) : FinanceRepository {

    override fun getAllTransactions(): Flow<List<Transaction>> {
        // Берем Flow<List<TransactionEntity>> и трансформируем (map) его в Flow<List<Transaction>>
        return dao.getAllTransactions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return dao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAccount(): Flow<Account?> {
        return dao.getAccount().map { it?.toDomain() }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: Transaction) {
        dao.deleteTransaction(transaction.toEntity())
    }

    override suspend fun addCategory(category: Category) {
        dao.insertCategory(category.toEntity())
    }

    override suspend fun updateAccount(account: Account) {
        dao.insertAccount(account.toEntity())
    }

    override suspend fun clearAllData() {
        dao.clearTransactions()
        dao.clearAccounts()
        // Сюда же можно добавить очистку DataStore, если нужно
    }
}