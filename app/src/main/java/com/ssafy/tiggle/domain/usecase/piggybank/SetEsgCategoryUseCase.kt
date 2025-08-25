package com.ssafy.tiggle.domain.usecase.piggybank

import com.ssafy.tiggle.domain.entity.piggybank.PiggyBank
import com.ssafy.tiggle.domain.repository.PiggyBankRepository
import javax.inject.Inject

class SetEsgCategoryUseCase @Inject constructor(
    private val repository: PiggyBankRepository
) {
    suspend operator fun invoke(categoryId: Int): Result<PiggyBank> {
        return repository.setEsgCategory(categoryId)
    }
}
