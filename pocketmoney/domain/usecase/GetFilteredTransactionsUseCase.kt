package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.data.preferences.SettingsManager
import com.example.pocketmoney.domain.models.TransactionType
import com.example.pocketmoney.domain.models.TransactionWithCategory
import com.example.pocketmoney.domain.repository.CurrencyRepository
import com.example.pocketmoney.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart

class GetFilteredTransactionsUseCase(
    private val financeRepository: FinanceRepository,
    private val currencyRepository: CurrencyRepository,
    private val settingsManager: SettingsManager
) {
    operator fun invoke(
        type: TransactionType?,
        categoryId: Long?,
        dateRange: Pair<Long, Long>?
    ): Flow<List<TransactionWithCategory>> {

        val safeRatesFlow = currencyRepository.allRatesFlow.onStart { emit(emptyList()) }

        val startDate = dateRange?.first
        val endDate = dateRange?.second

        val filteredTransactionsFlow = financeRepository.getFilteredTransactions(
            type = type,
            categoryId = categoryId,
            startDate = startDate,
            endDate = endDate
        )

        return combine(
            filteredTransactionsFlow,
            safeRatesFlow,
            settingsManager.currency
        ) { transactions, rates, targetCurrency ->
            val currentCurrencyRate = rates.find { it.code == targetCurrency }?.rate ?: 1.0

            transactions.map { item ->
                item.copy(
                    transaction = item.transaction.copy(
                        amount = item.transaction.amount * currentCurrencyRate
                    )
                )
            }
        }
    }
}