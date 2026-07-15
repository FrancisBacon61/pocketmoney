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

        repository.addTransaction(convertedTransaction)

        val currentAccount = repository.getAccount().firstOrNull()
            ?: Account(id = 1L, name = "Основной счет", currentBalance = 0.0)

        val newBalance = if (transaction.type == TransactionType.INCOME) {
            currentAccount.currentBalance + amountInRub
        } else {
            currentAccount.currentBalance - amountInRub
        }

        repository.updateAccount(currentAccount.copy(currentBalance = newBalance))
    }
}