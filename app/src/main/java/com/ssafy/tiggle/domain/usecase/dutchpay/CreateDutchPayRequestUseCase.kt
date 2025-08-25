package com.ssafy.tiggle.domain.usecase.dutchpay

import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequest
import com.ssafy.tiggle.domain.repository.DutchPayRepository
import javax.inject.Inject

class CreateDutchPayRequestUseCase @Inject constructor(
    private val dutchPayRepository: DutchPayRepository
) {
    suspend operator fun invoke(request: DutchPayRequest): Result<Unit> {
        return dutchPayRepository.createDutchPayRequest(request)
    }
}
