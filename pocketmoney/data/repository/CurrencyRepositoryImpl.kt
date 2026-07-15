package com.example.pocketmoney.data.repository

import com.example.pocketmoney.data.local.dao.CurrencyDao
import com.example.pocketmoney.data.local.entity.CurrencyRateEntity
import com.example.pocketmoney.data.remote.api.CurrencyApiService
import com.example.pocketmoney.data.preferences.SettingsManager
import com.example.pocketmoney.domain.models.CurrencyRate
import com.example.pocketmoney.domain.repository.CurrencyRepository
// 2. ВАЖНО: Импортируем ИНТЕРФЕЙС из слоя Domain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CurrencyRepositoryImpl(
    private val apiService: CurrencyApiService,
    private val currencyDao: CurrencyDao,
    private val settingsManager: SettingsManager
) : CurrencyRepository {

    private val supportedCurrencies = setOf("RUB", "USD", "EUR", "KZT")
    override val allRatesFlow: Flow<List<CurrencyRate>> = currencyDao.getAllRatesFlow().map { entities ->
        entities.map { CurrencyRate(code = it.code, rate = it.rate) }
    }

    override suspend fun refreshRates(baseCurrency: String) {
        val response = apiService.getLatestRates(baseCurrency)

        val filteredRates = response.rates.filterKeys { it in supportedCurrencies }

        val rateEntities = filteredRates.map { (code, rate) ->
            CurrencyRateEntity(code = code, rate = rate)
        }

        currencyDao.insertRates(rateEntities)

        val currentTimestamp = System.currentTimeMillis()
        settingsManager.saveLastUpdateTime(currentTimestamp)
    }

    override suspend fun getRate(code: String): Double {
        return currencyDao.getRateByCode(code) ?: 1.0
    }
    override suspend fun getLastUpdateTime(): Long {
        return settingsManager.lastCurrencyUpdate.first()
    }
}