package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.domain.models.Category
import com.example.pocketmoney.domain.repository.FinanceRepository
import kotlinx.coroutines.flow.Flow

class GetAllCategoriesUseCase(
    private val financeRepository: FinanceRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return financeRepository.getAllCategories()
    }
}