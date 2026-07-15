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
import com.example.pocketmoney.domain.usecase.AddCategoryUseCase

// ИМПОРТ UseCases
import com.example.pocketmoney.domain.usecase.AddTransactionUseCase
import com.example.pocketmoney.domain.usecase.DeleteTransactionUseCase
import com.example.pocketmoney.domain.usecase.GetAccountUseCase
import com.example.pocketmoney.domain.usecase.RefreshCurrencyRatesUseCase
import com.example.pocketmoney.domain.usecase.GetCurrencyRatesUseCase
import com.example.pocketmoney.domain.usecase.GetHomeDisplayDataUseCase
import com.example.pocketmoney.domain.usecase.GetRateUseCase
import com.example.pocketmoney.domain.usecase.GetAllCategoriesUseCase
import com.example.pocketmoney.domain.usecase.GetDefaultCategoryUseCase
import com.example.pocketmoney.domain.usecase.GetFilteredTransactionsUseCase
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

    single { SettingsManager(androidContext()) }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "pocket_money_db"
        )
            .addCallback(AppDatabase.getCallback())
            .fallbackToDestructiveMigration(false)
            .build()
    }

    single { get<AppDatabase>().financeDao() }
    single { get<AppDatabase>().currencyDao() }
    single { get<AppDatabase>().categoryDao() }

    singleOf(::FinanceRepositoryImpl) { bind<FinanceRepository>() }
    singleOf(::CurrencyRepositoryImpl) { bind<CurrencyRepository>() }

    factoryOf(::AddTransactionUseCase)
    factoryOf(::DeleteTransactionUseCase)
    factoryOf(::GetAccountUseCase)
    factoryOf(::RefreshCurrencyRatesUseCase)
    factoryOf(::GetCurrencyRatesUseCase)
    factoryOf(::GetRateUseCase)
    factoryOf(::GetHomeDisplayDataUseCase)
    factoryOf (::GetAllCategoriesUseCase)
    factoryOf (::AddCategoryUseCase)
    factoryOf (::GetFilteredTransactionsUseCase)
    factoryOf (::GetDefaultCategoryUseCase)

    viewModelOf(::HomeViewModel)
    viewModelOf (::TransactionsViewModel)
    viewModelOf(::SettingsViewModel)
}

val networkModule = module {
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

    single { CurrencyApiService(get()) }
}