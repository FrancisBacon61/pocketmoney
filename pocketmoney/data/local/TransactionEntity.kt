package com.example.pocketmoney.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pocketmoney.domain.models.Transaction
import com.example.pocketmoney.domain.models.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val categoryId: Long,
    val date: Long,
    val comment: String,
    val type: String // Храним "INCOME" или "EXPENSE"
)

// Перевод из базы в чистый Domain
fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        categoryId = categoryId,
        date = date,
        comment = comment,
        type = TransactionType.valueOf(type) // Превращаем строку обратно в Enum
    )
}

// Перевод из Domain в формат базы для сохранения
fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        categoryId = categoryId,
        date = date,
        comment = comment,
        type = type.name // Превращаем Enum в строку
    )
}