package com.example.pocketmoney.data.remote.api

import com.example.pocketmoney.data.remote.dto.CurrencyResponseDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class CurrencyApiService(private val client: HttpClient) {

    suspend fun getLatestRates(baseCurrency: String): CurrencyResponseDto {
        return client.get("https://open.er-api.com/v6/latest/$baseCurrency").body()
    }
}