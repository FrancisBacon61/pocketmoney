package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.data.preferences.SettingsManager
import com.example.pocketmoney.domain.models.HomeDisplayData
import com.example.pocketmoney.domain.models.TransactionType
import com.example.pocketmoney.domain.repository.CurrencyRepository
import com.example.pocketmoney.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart

class GetHomeDisplayDataUseCase(
    private val financeRepository: FinanceRepository,
    private val currencyRepository: CurrencyRepository,
    private val settingsManager: SettingsManager
) {
    operator fun invoke(): Flow<HomeDisplayData> {
        // Безопасный поток курсов: если БД пустая, принудительно эмитим пустой список, чтобы не вешать combine
        val safeRatesFlow = currencyRepository.allRatesFlow.onStart { emit(emptyList()) }

        // Для доходов/расходов нам нужны абсолютно ВСЕ транзакции из базы данных
        val recentTransactionsFlow = financeRepository.getRecentTransactions()

        return combine(
            recentTransactionsFlow,
            financeRepository.getAccount(), // Поток аккаунта из Room
            safeRatesFlow,
            settingsManager.currency,
            settingsManager.isBalanceHidden
        ) { transactions, account, rates, targetCurrency, isHidden ->

            // 1. Ищем курс выбранной валюты (если кэш пуст или это RUB — берем 1.0)
            val currentCurrencyRate = rates.find { it.code == targetCurrency }?.rate ?: 1.0

            // 2. Конвертируем баланс аккаунта (который хранится в RUB) в целевую валюту
            val rawBalanceInRub = account?.currentBalance ?: 0.0
            val convertedBalance = rawBalanceInRub * currentCurrencyRate

            // 3. Считаем ОБЩИЕ доходы и расходы по ВСЕЙ истории (в RUB) и конвертируем финальную сумму
            val rawIncomeRub = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val rawExpensesRub = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

            // 4. Переводим списочные транзакции по курсу для отображения последних записей
            val convertedTransactions = transactions.take(20).map { tx ->
                tx.copy(amount = tx.amount * currentCurrencyRate)
            }

            HomeDisplayData(
                transactions = convertedTransactions,
                totalBalance = convertedBalance,
                totalIncome = rawIncomeRub * currentCurrencyRate,
                totalExpenses = rawExpensesRub * currentCurrencyRate,
                currency = targetCurrency,
                isBalanceHidden = isHidden
            )
        }
    }
}