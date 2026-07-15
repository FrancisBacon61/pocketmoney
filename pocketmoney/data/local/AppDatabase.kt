package com.example.pocketmoney.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pocketmoney.data.local.dao.CategoryDao
import com.example.pocketmoney.data.local.dao.CurrencyDao
import com.example.pocketmoney.data.local.dao.FinanceDao
import com.example.pocketmoney.data.local.entity.AccountEntity
import com.example.pocketmoney.data.local.entity.CurrencyRateEntity
import com.example.pocketmoney.data.local.entity.TransactionEntity
import com.example.pocketmoney.data.local.entity.CategoryEntity
@Database(
    entities = [
        TransactionEntity::class,
        AccountEntity::class,
        CurrencyRateEntity::class,
        CategoryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun financeDao(): FinanceDao
    abstract fun currencyDao(): CurrencyDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        fun getCallback(): Callback {
            return object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    try {
                        db.execSQL(
                            "INSERT INTO accounts (id, name, currentBalance) VALUES (1, 'Основной счет', 0.0)"
                        )

                        db.execSQL("INSERT INTO categories (id, name, icon, colorHex) VALUES (1, 'Продукты', '🍔', '#FF5722')")
                        db.execSQL("INSERT INTO categories (id, name, icon, colorHex) VALUES (2, 'Транспорт', '🚗', '#2196F3')")
                        db.execSQL("INSERT INTO categories (id, name, icon, colorHex) VALUES (3, 'Зарплата', '💰', '#4CAF50')")
                        db.execSQL("INSERT INTO categories (id, name, icon, colorHex) VALUES (4, 'Развлечения', '🎬', '#9C27B0')")
                        db.execSQL("INSERT INTO categories (id, name, icon, colorHex) VALUES (5, 'Другое', '📦', '#607D8B')")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}