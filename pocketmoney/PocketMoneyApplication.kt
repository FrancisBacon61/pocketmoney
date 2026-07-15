package com.example.pocketmoney

import android.app.Application
import com.example.pocketmoney.di.appModule
import com.example.pocketmoney.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PocketMoneyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@PocketMoneyApplication)
            modules(appModule, networkModule)
        }
    }
}