package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.domain.repository.FinanceRepository
import com.example.pocketmoney.domain.models.Category
import kotlinx.coroutines.flow.Flow

class GetCategoriesUseCase(private val repository: FinanceRepository) {
    operator fun invoke(): Flow<List<Category>> = repository.getAllCategories()
}