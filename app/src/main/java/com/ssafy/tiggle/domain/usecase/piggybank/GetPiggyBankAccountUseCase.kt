package com.ssafy.tiggle.domain.usecase.piggybank

import com.ssafy.tiggle.domain.entity.piggybank.MainAccount
import com.ssafy.tiggle.domain.entity.piggybank.PiggyBankAccount
import com.ssafy.tiggle.domain.repository.PiggyBankRepository
import javax.inject.Inject

class GetPiggyBankAccountUseCase @Inject constructor(
    private val repository: PiggyBankRepository
) {
    suspend operator fun invoke():Result<PiggyBankAccount> {
        return repository.getPiggyBankAccount()
    }
}
