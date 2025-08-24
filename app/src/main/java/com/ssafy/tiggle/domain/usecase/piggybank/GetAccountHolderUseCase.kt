package com.ssafy.tiggle.domain.usecase.piggybank

import com.ssafy.tiggle.domain.entity.piggybank.AccountHolder
import com.ssafy.tiggle.domain.repository.PiggyBankRepository
import javax.inject.Inject

class GetAccountHolderUseCase @Inject constructor(
    private val repository: PiggyBankRepository
) {
    suspend operator fun invoke(accountNoRaw: String): Result<AccountHolder> {
        val sanitized = accountNoRaw.filter { it.isDigit() }
        if (sanitized.isBlank()) {
            return Result.failure(IllegalArgumentException("계좌번호를 입력해주세요."))
        }
        return repository.getAccountHolder(sanitized)
    }
}