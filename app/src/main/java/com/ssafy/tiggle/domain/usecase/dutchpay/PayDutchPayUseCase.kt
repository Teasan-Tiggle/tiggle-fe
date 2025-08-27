package com.ssafy.tiggle.domain.usecase.dutchpay

import com.ssafy.tiggle.domain.repository.DutchPayRepository
import javax.inject.Inject

class PayDutchPayUseCase @Inject constructor(
    private val dutchPayRepository: DutchPayRepository
) {
    suspend operator fun invoke(dutchPayId: Long, payMore: Boolean): Result<Unit> {
        return dutchPayRepository.payDutchPay(dutchPayId, payMore)
    }
}
