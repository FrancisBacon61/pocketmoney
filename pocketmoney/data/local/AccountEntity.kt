package com.example.pocketmoney.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pocketmoney.domain.models.Account

@Entity(tableName = "accounts")
data class AccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val currentBalance: Double
)

// Переводчик из БД-модели в чистую Domain-модель
fun AccountEntity.toDomain(): Account {
    return Account(id = id, name = name, currentBalance = currentBalance)
}

// Переводчик из Domain-модели в БД-модель
fun Account.toEntity(): AccountEntity {
    return AccountEntity(id = id, name = name, currentBalance = currentBalance)
}