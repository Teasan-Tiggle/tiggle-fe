package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.domain.entity.growth.GrowthResult
import com.ssafy.tiggle.domain.entity.growth.HeartResult

interface GrowthRepository {

    suspend fun getGrowthResult(): Result<GrowthResult>
    suspend fun clickHeart(): Result<HeartResult>
}
