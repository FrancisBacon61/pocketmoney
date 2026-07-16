package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.domain.models.CurrencyRate
import com.example.pocketmoney.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCurrencyRatesUseCase(
    private val currencyRepository: CurrencyRepository
) {
    operator fun invoke(): Flow<List<CurrencyRate>> {
        return currencyRepository.allRatesFlow.map { rawList ->
            rawList
                .filter { it.code != "RUB" }
                .map { item ->
                    val normalRate = if (item.rate > 0) 1.0 / item.rate else 0.0

                    item.copy(rate = normalRate)
                }
        }
    }
}