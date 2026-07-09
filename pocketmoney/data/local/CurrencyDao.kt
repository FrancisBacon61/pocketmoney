package com.example.pocketmoney.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    // Сохраняем пачкой новые курсы. Если такой код уже есть — перезаписываем актуальным курсом
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<CurrencyRateEntity>)

    // Получаем все курсы потоком для реактивного UI
    @Query("SELECT * FROM currency_rates")
    fun getAllRatesFlow(): Flow<List<CurrencyRateEntity>>

    // Метод для быстрой конвертации «на лету» внутри бизнес-логики
    @Query("SELECT rate FROM currency_rates WHERE code = :code LIMIT 1")
    suspend fun getRateByCode(code: String): Double?
}