package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.domain.models.Account
import com.example.pocketmoney.domain.models.Transaction
import com.example.pocketmoney.domain.models.TransactionType
import com.example.pocketmoney.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.firstOrNull

class AddTransactionUseCase(
    private val repository: FinanceRepository,
    private val getRateUseCase: GetRateUseCase
) {
    suspend operator fun invoke(transaction: Transaction, currency: String) {
        val rate = getRateUseCase(currency)
        val amountInRub = if (rate != 0.0) transaction.amount / rate else transaction.amount
        val convertedTransaction = transaction.copy(amount = amountInRub)

        // 1. Сохраняем транзакцию
        repository.addTransaction(convertedTransaction)

        // 2. Получаем аккаунт. Если его нет — создаем дефолтный основной счет
        val currentAccount = repository.getAccount().firstOrNull()
            ?: Account(id = 1L, name = "Основной счет", currentBalance = 0.0)

        // 3. Высчитываем новый баланс
        val newBalance = if (transaction.type == TransactionType.INCOME) {
            currentAccount.currentBalance + amountInRub
        } else {
            currentAccount.currentBalance - amountInRub
        }

        // 4. Обновляем или вставляем аккаунт обратно в БД
        repository.updateAccount(currentAccount.copy(currentBalance = newBalance))
    }
}