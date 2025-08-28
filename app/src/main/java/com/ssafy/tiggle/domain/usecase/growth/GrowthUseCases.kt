package com.ssafy.tiggle.domain.usecase.growth

import javax.inject.Inject

data class GrowthUseCases @Inject constructor(
    val getGrowthResultUseCase: GetGrowthResultUseCase
)