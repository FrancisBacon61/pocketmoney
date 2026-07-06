package com.example.pocketmoney.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsManager(private val context: Context) {

    private val currencyKey = stringPreferencesKey("currency")
    private val hideBalanceKey = booleanPreferencesKey("hide_balance")
    private val lastUpdateKey = longPreferencesKey("last_currency_update")

    // Поток настроек для UI
    val currency: Flow<String> = context.dataStore.data.map { it[currencyKey] ?: "RUB" }
    val isBalanceHidden: Flow<Boolean> = context.dataStore.data.map { it[hideBalanceKey] ?: false }

    val lastCurrencyUpdate: Flow<Long> = context.dataStore.data.map { it[lastUpdateKey] ?: 0L }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { it[currencyKey] = currency }
    }

    suspend fun setBalanceHidden(hidden: Boolean) {
        context.dataStore.edit { it[hideBalanceKey] = hidden }
    }
    suspend fun saveLastUpdateTime(timestamp: Long) {
        context.dataStore.edit {
            it[lastUpdateKey] = timestamp
        }
    }
}