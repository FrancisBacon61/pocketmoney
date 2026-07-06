package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.domain.repository.CurrencyRepository

class GetRateUseCase(
    private val currencyRepository: CurrencyRepository
) {
    // Позволяет быстро получить коэффициент конкретной валюты для математических расчетов
    suspend operator fun invoke(code: String): Double {
        return currencyRepository.getRate(code)
    }
}