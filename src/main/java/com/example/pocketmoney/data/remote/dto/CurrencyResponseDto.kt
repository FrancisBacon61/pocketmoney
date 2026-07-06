package com.example.pocketmoney.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class CurrencyResponseDto(
    @SerialName("base_code") val baseCode: String,
    @SerialName("rates") val rates: Map<String, Double>,
    @SerialName("time_last_update_unix") val lastUpdate: Long
)