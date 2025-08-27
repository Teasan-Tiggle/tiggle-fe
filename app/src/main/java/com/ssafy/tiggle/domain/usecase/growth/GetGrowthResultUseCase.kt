package com.ssafy.tiggle.domain.usecase.growth

import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequest
import com.ssafy.tiggle.domain.entity.growth.GrowthResult
import com.ssafy.tiggle.domain.repository.DutchPayRepository
import com.ssafy.tiggle.domain.repository.GrowthRepository
import javax.inject.Inject

class GetGrowthResultUseCase @Inject constructor(
    private val growthRepository: GrowthRepository
) {
    suspend operator fun invoke(): Result<GrowthResult> {
        return growthRepository.getGrowthResult()
    }
}
