package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.domain.repository.FinanceRepository
import com.example.pocketmoney.domain.models.Transaction
import com.example.pocketmoney.domain.models.TransactionType // Импортируем ваш Enum (INCOME, EXPENSE)
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTransactionsUseCase(
    private val repository: FinanceRepository
) {
    // Внутрь invoke теперь передаем тип транзакции. Если он null — фильтрации по типу нет
    operator fun invoke(type: TransactionType? = null): Flow<List<Transaction>> {
        return repository.getAllTransactions().map { transactions ->
            if (type != null) {
                // Фильтруем по типу: Доход или Расход
                transactions.filter { it.type == type }
            } else {
                // Отдаем весь список целиком
                transactions
            }
        }
    }
}