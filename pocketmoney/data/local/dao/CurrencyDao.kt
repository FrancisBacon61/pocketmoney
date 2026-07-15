package com.example.pocketmoney.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pocketmoney.data.local.entity.CurrencyRateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRates(rates: List<CurrencyRateEntity>)

    @Query("SELECT * FROM currency_rates")
    fun getAllRatesFlow(): Flow<List<CurrencyRateEntity>>

    @Query("SELECT rate FROM currency_rates WHERE code = :code LIMIT 1")
    suspend fun getRateByCode(code: String): Double?
}