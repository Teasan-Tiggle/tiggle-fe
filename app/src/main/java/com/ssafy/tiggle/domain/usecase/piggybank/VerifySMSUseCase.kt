package com.ssafy.tiggle.domain.usecase.piggybank

import com.ssafy.tiggle.data.model.piggybank.response.VerifySMSResponseDto
import com.ssafy.tiggle.domain.repository.PiggyBankRepository
import javax.inject.Inject

class VerifySMSUseCase @Inject constructor(
    val repository: PiggyBankRepository
) {
    suspend operator fun invoke(phone: String, code: String, purpose: String): Result<VerifySMSResponseDto> {
        return repository.verifySMS(phone, code, purpose)
    }
}
