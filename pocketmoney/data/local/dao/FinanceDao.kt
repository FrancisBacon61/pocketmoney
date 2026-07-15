package com.example.pocketmoney.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pocketmoney.data.local.TransactionWithCategoryEntity
import com.example.pocketmoney.data.local.entity.AccountEntity
import com.example.pocketmoney.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM accounts LIMIT 1")
    fun getAccount(): Flow<AccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM accounts")
    suspend fun clearAccounts()

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactionsWithCategoryFlow(): Flow<List<TransactionWithCategoryEntity>>

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT 20")
    fun getRecentTransactionsWithCategoryFlow(): Flow<List<TransactionWithCategoryEntity>>

    @androidx.room.Transaction
    @Query("""
        SELECT * FROM transactions 
        WHERE (:type IS NULL OR type = :type) 
          AND (:categoryId IS NULL OR categoryId = :categoryId)
          AND (:startDate IS NULL OR :endDate IS NULL OR date BETWEEN :startDate AND :endDate)
        ORDER BY date DESC
    """)
    fun getFilteredTransactionsFlow(
        type: String?,
        categoryId: Long?,
        startDate: Long?,
        endDate: Long?
    ): Flow<List<TransactionWithCategoryEntity>>
}