package com.example.pocketmoney.data.repository

import com.example.pocketmoney.data.local.dao.FinanceDao
import com.example.pocketmoney.data.local.dao.CategoryDao
import com.example.pocketmoney.data.local.entity.* // Подтягивает .toDomain() и .toEntity()
import com.example.pocketmoney.domain.repository.FinanceRepository
import com.example.pocketmoney.domain.models.Account
import com.example.pocketmoney.domain.models.Category
import com.example.pocketmoney.domain.models.Transaction
import com.example.pocketmoney.domain.models.TransactionType
import com.example.pocketmoney.domain.models.TransactionWithCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FinanceRepositoryImpl(
    private val dao: FinanceDao,
    private val categoryDao: CategoryDao
) : FinanceRepository {

    override fun getRecentTransactions(): Flow<List<TransactionWithCategory>> {
        return dao.getRecentTransactionsWithCategoryFlow().map { entities ->
            entities.map { entity ->
                TransactionWithCategory(
                    transaction = entity.transaction.toDomain(),
                    category = entity.category?.toDomain()
                )
            }
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

    override suspend fun updateAccount(account: Account) {
        dao.insertAccount(account.toEntity())
    }

    override suspend fun clearAllData() {
        dao.clearTransactions()
        dao.clearAccounts()
    }

    override fun getFilteredTransactions(
        type: TransactionType?,
        categoryId: Long?,
        startDate: Long?,
        endDate: Long?
    ): Flow<List<TransactionWithCategory>> {
        val typeString = type?.name
        return dao.getFilteredTransactionsFlow(typeString, categoryId, startDate, endDate).map { entities ->
            entities.map { entity ->
                TransactionWithCategory(
                    transaction = entity.transaction.toDomain(),
                    category = entity.category?.toDomain()
                )
            }
        }
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategoriesFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun getDefaultCategory(): Category? {
        return categoryDao.getCategoryById(DEFAULT_CATEGORY_ID)?.toDomain()
    }

    companion object {
        private const val DEFAULT_CATEGORY_ID = 5L
    }
}