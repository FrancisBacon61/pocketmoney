package com.example.pocketmoney.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey
    val code: String, // Код валюты (например, "USD", "EUR", "EUR")
    val rate: Double  // Значение курса относительно базовой валюты
)