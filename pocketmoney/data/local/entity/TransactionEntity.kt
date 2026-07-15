package com.example.pocketmoney.data.local.entity

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
    val type: String // "INCOME" или "EXPENSE"
)

fun TransactionEntity.toDomain(): Transaction {
    return Transaction(
        id = id,
        amount = amount,
        categoryId = categoryId,
        date = date,
        comment = comment,
        type = TransactionType.valueOf(type)
    )
}

fun Transaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        categoryId = categoryId,
        date = date,
        comment = comment,
        type = type.name
    )
}