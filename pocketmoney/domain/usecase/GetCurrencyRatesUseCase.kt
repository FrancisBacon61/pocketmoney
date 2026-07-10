package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.data.local.CurrencyRateEntity
import com.example.pocketmoney.domain.models.CurrencyRate
import com.example.pocketmoney.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCurrencyRatesUseCase(
    private val currencyRepository: CurrencyRepository
) {
    operator fun invoke(): Flow<List<CurrencyRate>> { // Класс остается СТАРЫМ
        return currencyRepository.allRatesFlow.map { rawList ->
            rawList
                .filter { it.code != "RUB" } // 1. Просто убираем рубль из списка
                .map { item ->
                    // 2. Считаем нормальный курс (1 / коэффициент)
                    val normalRate = if (item.rate > 0) 1.0 / item.rate else 0.0

                    // 3. Возвращаем ТУ ЖЕ САМУЮ модельку, просто подменив внутри неё значение rate
                    item.copy(rate = normalRate)
                }
        }
    }
}