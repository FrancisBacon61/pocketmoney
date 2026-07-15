package com.example.pocketmoney.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pocketmoney.domain.models.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String,
    val colorHex: String
)

fun CategoryEntity.toDomain(): Category {
    return Category(id = id, name = name, icon = icon, colorHex = colorHex)
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(id = id, name = name, icon = icon, colorHex = colorHex)
}