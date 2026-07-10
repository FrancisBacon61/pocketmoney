package com.example.pocketmoney.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FinanceDao {
    // Транзакции
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT 20")
    fun getRecentTransactionsFlow(): Flow<List<TransactionEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    // Аккаунт/Счет
    @Query("SELECT * FROM accounts LIMIT 1")
    fun getAccount(): Flow<AccountEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity)

    // Очистка данных
    @Query("DELETE FROM transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM accounts")
    suspend fun clearAccounts()
}