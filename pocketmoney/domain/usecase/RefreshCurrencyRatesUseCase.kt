package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.domain.repository.CurrencyRepository

class RefreshCurrencyRatesUseCase(
    private val repository: CurrencyRepository
) {
    suspend operator fun invoke(forceRefresh: Boolean = false) {
        if (forceRefresh) {
            repository.refreshRates("RUB")
            return
        }

        val lastUpdateTime = repository.getLastUpdateTime()
        val currentTime = System.currentTimeMillis()
        val oneDayInMillis = 24 * 60 * 60 * 1000L

        if (currentTime - lastUpdateTime > oneDayInMillis || lastUpdateTime == 0L) {
            repository.refreshRates("RUB")
        }
    }
}