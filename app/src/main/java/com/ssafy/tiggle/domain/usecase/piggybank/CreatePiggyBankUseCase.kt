package com.ssafy.tiggle.domain.usecase.piggybank

import com.ssafy.tiggle.domain.repository.PiggyBankRepository
import javax.inject.Inject

class CreatePiggyBankUseCase @Inject constructor(
    private val repository: PiggyBankRepository
) {
    suspend operator fun invoke(
        name: String,
        targetAmount: Long,
        esgCategoryId: Int
    ): Result<Unit> {
        return repository.createPiggyBank(name, targetAmount, esgCategoryId)
    }
}
