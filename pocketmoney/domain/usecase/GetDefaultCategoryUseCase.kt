package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.domain.models.Category
import com.example.pocketmoney.domain.repository.FinanceRepository

class GetDefaultCategoryUseCase(
    private val repository: FinanceRepository
) {
    suspend operator fun invoke(): Category? {
        return repository.getDefaultCategory()
    }
}