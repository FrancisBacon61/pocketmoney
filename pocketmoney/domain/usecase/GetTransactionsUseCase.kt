package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.domain.repository.FinanceRepository
import com.example.pocketmoney.domain.models.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetTransactionsUseCase(
    private val repository: FinanceRepository
) {
    // Внутрь invoke мы можем передать ID категории. Если он null — фильтрации нет
    operator fun invoke(categoryId: Long? = null): Flow<List<Transaction>> {
        return repository.getAllTransactions().map { transactions ->
            if (categoryId != null) {
                // Если категория выбрана, фильтруем список
                transactions.filter { it.categoryId == categoryId }
            } else {
                // Если не выбрана — отдаем весь список целиком
                transactions
            }
        }
    }
}