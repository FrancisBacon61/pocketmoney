package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.domain.repository.FinanceRepository
import com.example.pocketmoney.domain.models.Transaction
import com.example.pocketmoney.domain.models.TransactionType
import kotlinx.coroutines.flow.firstOrNull

class DeleteTransactionUseCase(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(transaction: Transaction) {
        // 1. Удаляем транзакцию из базы данных
        repository.deleteTransaction(transaction)

        // 2. Получаем текущий аккаунт для перерасчета баланса
        val currentAccount = repository.getAccount().firstOrNull()

        if (currentAccount != null) {
            // 3. Возвращаем деньги на счет (обратная логика)
            val newBalance = if (transaction.type == TransactionType.EXPENSE) {
                currentAccount.currentBalance + transaction.amount // Если удалили расход — баланс вырос
            } else {
                currentAccount.currentBalance - transaction.amount // Если удалили доход — баланс упал
            }

            // 4. Сохраняем обновленный аккаунт
            repository.updateAccount(currentAccount.copy(currentBalance = newBalance))
        }
    }
}