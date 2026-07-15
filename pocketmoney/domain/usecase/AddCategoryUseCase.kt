package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.domain.models.Category
import com.example.pocketmoney.domain.repository.FinanceRepository

class AddCategoryUseCase(
    private val financeRepository: FinanceRepository
) {
    suspend operator fun invoke(category: Category): Long {
        return financeRepository.insertCategory(category)
    }
}