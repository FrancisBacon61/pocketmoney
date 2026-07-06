package com.example.pocketmoney.di

import androidx.room.Room
import com.example.pocketmoney.data.local.AppDatabase
import com.example.pocketmoney.data.preferences.SettingsManager
import com.example.pocketmoney.data.remote.api.CurrencyApiService

// 1. ИМПОРТИРУЕМ РЕПОЗИТОРИИ (И интерфейсы, и их реализации)
import com.example.pocketmoney.domain.repository.FinanceRepository
import com.example.pocketmoney.data.repository.FinanceRepositoryImpl

import com.example.pocketmoney.domain.repository.CurrencyRepository // Интерфейс из Domain
import com.example.pocketmoney.data.repository.CurrencyRepositoryImpl // Реализация из Data

// ИМПОРТ UseCases
import com.example.pocketmoney.domain.usecase.AddTransactionUseCase
import com.example.pocketmoney.domain.usecase.DeleteTransactionUseCase
import com.example.pocketmoney.domain.usecase.GetTransactionsUseCase
import com.example.pocketmoney.domain.usecase.GetAccountUseCase
import com.example.pocketmoney.domain.usecase.GetCategoriesUseCase
import com.example.pocketmoney.domain.usecase.RefreshCurrencyRatesUseCase
import com.example.pocketmoney.domain.usecase.GetCurrencyRatesUseCase
import com.example.pocketmoney.domain.usecase.GetHomeDisplayDataUseCase
import com.example.pocketmoney.domain.usecase.GetRateUseCase

// ИМПОРТ ViewModels
import com.example.pocketmoney.ui.home.HomeViewModel
import com.example.pocketmoney.ui.transactions.TransactionsViewModel
import com.example.pocketmoney.ui.settings.SettingsViewModel

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

val appModule = module {

    // 1. Слой данных: Хранилище настроек (DataStore) [cite: 7]
    single { SettingsManager(androidContext()) }

    // 2. Слой данных: База данных Room [cite: 6]
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "pocket_money_db"
        )
            .addCallback(AppDatabase.getCallback())
            .build()
    }

    // 3. Слой данных: Предоставляем DAO для репозиториев
    single { get<AppDatabase>().financeDao() }
    single { get<AppDatabase>().currencyDao() }

    // 4. Слой репозиториев: Связываем интерфейсы из Domain и реализации из Data [cite: 3]
    singleOf(::FinanceRepositoryImpl) { bind<FinanceRepository>() }
    singleOf(::CurrencyRepositoryImpl) { bind<CurrencyRepository>() } // ИСПРАВЛЕНО: Связали валютный репозиторий!

    // 5. Слой бизнес-логики (Domain): Регистрируем UseCases
    factoryOf(::AddTransactionUseCase)
    factoryOf(::DeleteTransactionUseCase)
    factoryOf(::GetTransactionsUseCase)
    factoryOf(::GetAccountUseCase)
    factoryOf(::GetCategoriesUseCase)
    factoryOf(::RefreshCurrencyRatesUseCase)
    factoryOf(::GetCurrencyRatesUseCase)
    factoryOf(::GetRateUseCase)
    factoryOf(::GetHomeDisplayDataUseCase)

    // 6. Слой UI: Регистрируем ViewModels
    viewModelOf(::HomeViewModel)
    viewModelOf(::TransactionsViewModel)
    viewModelOf(::SettingsViewModel)
}

val networkModule = module {
    // Создаем и настраиваем HttpClient (Ktor) [cite: 6]
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    coerceInputValues = true
                })
            }
        }
    }

    // Регистрируем наш API-сервис [cite: 6]
    single { CurrencyApiService(get()) }
}