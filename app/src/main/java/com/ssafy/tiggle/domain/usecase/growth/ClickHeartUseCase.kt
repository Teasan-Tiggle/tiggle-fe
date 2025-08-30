package com.ssafy.tiggle.domain.usecase.growth

import com.ssafy.tiggle.domain.entity.growth.HeartResult
import com.ssafy.tiggle.domain.repository.GrowthRepository
import javax.inject.Inject

class ClickHeartUseCase @Inject constructor(
    private val growthRepository: GrowthRepository
) {
    suspend operator fun invoke(): Result<HeartResult> {
        return growthRepository.clickHeart()
    }
}
