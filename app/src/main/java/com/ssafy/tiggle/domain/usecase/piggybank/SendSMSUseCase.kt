package com.ssafy.tiggle.domain.usecase.piggybank

import com.ssafy.tiggle.domain.repository.PiggyBankRepository
import javax.inject.Inject

class SendSMSUseCase @Inject constructor(
    private val repository: PiggyBankRepository
) {
    suspend operator fun invoke(
        phone: String,
        purpose: String
    ): Result<Unit> {
        return repository.sendSMS(phone, purpose)
    }
}
