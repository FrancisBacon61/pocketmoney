package com.example.pocketmoney.domain.usecase

import com.example.pocketmoney.domain.repository.FinanceRepository
import com.example.pocketmoney.domain.models.Account
import kotlinx.coroutines.flow.Flow

class GetAccountUseCase(private val repository: FinanceRepository) {
    operator fun invoke(): Flow<Account?> = repository.getAccount()
}
