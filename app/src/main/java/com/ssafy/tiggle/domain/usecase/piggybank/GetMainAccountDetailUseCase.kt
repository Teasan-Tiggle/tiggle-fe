package com.ssafy.tiggle.domain.usecase.piggybank

import com.ssafy.tiggle.domain.entity.piggybank.MainAccountDetail
import com.ssafy.tiggle.domain.repository.PiggyBankRepository
import javax.inject.Inject

class GetMainAccountDetailUseCase @Inject constructor(
    private val repository: PiggyBankRepository
) {
    suspend operator fun invoke(
        accountNo: String,
        cursor: String? = null
    ): Result<MainAccountDetail> {
        return repository.getTransactions(accountNo, cursor)
    }
}