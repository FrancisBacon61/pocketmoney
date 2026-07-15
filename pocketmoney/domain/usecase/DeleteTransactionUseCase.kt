package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.domain.repository.FinanceRepository
import com.example.pocketmoney.domain.models.Transaction
import com.example.pocketmoney.domain.models.TransactionType
import kotlinx.coroutines.flow.firstOrNull

class DeleteTransactionUseCase(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        repository.deleteTransaction(transaction)

        val currentAccount = repository.getAccount().firstOrNull()

        if (currentAccount != null) {
            val newBalance = if (transaction.type == TransactionType.EXPENSE) {
                currentAccount.currentBalance + transaction.amount
            } else {
                currentAccount.currentBalance - transaction.amount
            }

            // 4. Сохраняем обновленный аккаунт
            repository.updateAccount(currentAccount.copy(currentBalance = newBalance))
        }
    }
}