// 1. Пакет остается DATA, так как этот класс работает с БД, сетью и преференсами
package com.example.pocketmoney.data.repository

import com.example.pocketmoney.data.local.CurrencyDao
import com.example.pocketmoney.data.local.CurrencyRateEntity
import com.example.pocketmoney.data.remote.api.CurrencyApiService
import com.example.pocketmoney.data.preferences.SettingsManager
import com.example.pocketmoney.domain.models.CurrencyRate
import com.example.pocketmoney.domain.repository.CurrencyRepository
// 2. ВАЖНО: Импортируем ИНТЕРФЕЙС из слоя Domain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// 3. Переименовываем класс в CurrencyRepositoryImpl и наследуемся от интерфейса (: CurrencyRepository)
class CurrencyRepositoryImpl(
    private val apiService: CurrencyApiService,
    private val currencyDao: CurrencyDao,
    private val settingsManager: SettingsManager
) : CurrencyRepository { // <- Эта строчка связывает слой Data со слоем Domain

    // 4. Добавляем ключевое слово override, так как эти свойства и функции описаны в интерфейсе
    override val allRatesFlow: Flow<List<CurrencyRate>> = currencyDao.getAllRatesFlow().map { entities ->
        entities.map { CurrencyRate(code = it.code, rate = it.rate) }
    }

    override suspend fun refreshRates(baseCurrency: String) {
        try {
            val response = apiService.getLatestRates(baseCurrency)
            val rateEntities = response.rates.map { (code, rate) ->
                CurrencyRateEntity(code = code, rate = rate)
            }
            currencyDao.insertRates(rateEntities)

            val currentTimestamp = System.currentTimeMillis()
            settingsManager.saveLastUpdateTime(currentTimestamp)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getRate(code: String): Double {
        return currencyDao.getRateByCode(code) ?: 1.0
    }
}