package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.domain.repository.CurrencyRepository

class RefreshCurrencyRatesUseCase(
    private val repository: CurrencyRepository
) {
    // Убираем параметр currency, всегда обновляем относительно RUB
    suspend operator fun invoke() {
        repository.refreshRates("RUB")
    }
}