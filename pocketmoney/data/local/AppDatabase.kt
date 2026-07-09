package com.example.pocketmoney.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [TransactionEntity::class, AccountEntity::class, CurrencyRateEntity::class], // Не забудь добавить сюда AccountEntity, когда создашь его
    version = 2,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun financeDao(): FinanceDao
    abstract fun currencyDao(): CurrencyDao
    companion object {
        // Создаем Callback для базы данных
        fun getCallback(): Callback {
            return object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    // Запуск корутины для безопасной вставки данных в фоновом потоке
                    CoroutineScope(Dispatchers.IO).launch {
                        // Вставляем дефолтную категорию напрямую через SQL-запрос при создании БД
                        db.execSQL(
                            "INSERT INTO categories (id, name, icon, color) VALUES (1, 'Общее', 'wallet', '#4CAF50')"
                        )

                        // Заодно сразу создадим один главный аккаунт (счет), чтобы баланс не был null!
                        db.execSQL(
                            "INSERT INTO accounts (id, name, currentBalance) VALUES (1, 'Основной счет', 0.0)"
                        )
                    }
                }
            }
        }
    }
}