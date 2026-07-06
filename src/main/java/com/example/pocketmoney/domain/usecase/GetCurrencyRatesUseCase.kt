package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.data.local.CurrencyRateEntity
import com.example.pocketmoney.domain.models.CurrencyRate
import com.example.pocketmoney.domain.repository.CurrencyRepository
import kotlinx.coroutines.flow.Flow

class GetCurrencyRatesUseCase(
    private val currencyRepository: CurrencyRepository
) {
    // Возвращает живой поток курсов валют из локальной базы данных Room
    operator fun invoke(): Flow<List<CurrencyRate>> = currencyRepository.allRatesFlow
}