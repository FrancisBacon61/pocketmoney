package com.example.pocketmoney.domain.repository

import com.example.pocketmoney.domain.models.CurrencyRate
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    // Поток всех курсов валют из базы данных для реактивного UI
    val allRatesFlow: Flow<List<CurrencyRate>>

    // Функция принудительного обновления курсов через сеть Ktor
    suspend fun refreshRates(baseCurrency: String)

    // Разовое получение коэффициента конкретной валюты
    suspend fun getRate(code: String): Double
    suspend fun getLastUpdateTime(): Long
}