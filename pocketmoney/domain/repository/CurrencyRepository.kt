package com.example.pocketmoney.domain.repository

import com.example.pocketmoney.domain.models.CurrencyRate
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    val allRatesFlow: Flow<List<CurrencyRate>>

    suspend fun refreshRates(baseCurrency: String)

    suspend fun getRate(code: String): Double
    suspend fun getLastUpdateTime(): Long
}