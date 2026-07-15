package com.example.pocketmoney.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey
    val code: String,
    val rate: Double  // Значение курса относительно базовой валюты
)