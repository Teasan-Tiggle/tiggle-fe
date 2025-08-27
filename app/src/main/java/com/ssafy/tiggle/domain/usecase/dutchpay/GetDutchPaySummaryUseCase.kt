package com.ssafy.tiggle.domain.usecase.dutchpay

import com.ssafy.tiggle.domain.entity.dutchpay.DutchPaySummary
import com.ssafy.tiggle.domain.repository.DutchPayRepository
import javax.inject.Inject

class GetDutchPaySummaryUseCase @Inject constructor(
    private val dutchPayRepository: DutchPayRepository
) {
    suspend operator fun invoke(): Result<DutchPaySummary> {
        return dutchPayRepository.getDutchPaySummary()
    }
}
