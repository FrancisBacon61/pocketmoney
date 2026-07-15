package com.example.pocketmoney.data.local

import androidx.room.Embedded
import androidx.room.Relation
import com.example.pocketmoney.data.local.entity.CategoryEntity
import com.example.pocketmoney.data.local.entity.TransactionEntity

data class TransactionWithCategoryEntity(
    @Embedded
    val transaction: TransactionEntity,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: CategoryEntity?
)