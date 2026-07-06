package com.example.pocketmoney.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pocketmoney.domain.models.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String,
    val color: String
)

fun CategoryEntity.toDomain(): Category {
    return Category(id, name, icon, color)
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(id, name, icon, color)
}