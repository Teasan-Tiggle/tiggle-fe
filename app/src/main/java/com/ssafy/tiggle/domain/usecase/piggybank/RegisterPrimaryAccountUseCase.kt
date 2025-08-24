package com.ssafy.tiggle.domain.usecase.piggybank

import com.ssafy.tiggle.domain.repository.PiggyBankRepository
import javax.inject.Inject

class RegisterPrimaryAccountUseCase @Inject constructor(
    private val repository: PiggyBankRepository
) {
    suspend operator fun invoke(accountNo: String, token: String) =
        repository.registerPrimaryAccount(accountNo, token)
}