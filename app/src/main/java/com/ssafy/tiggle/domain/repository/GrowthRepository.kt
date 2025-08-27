package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.domain.entity.growth.GrowthResult

interface GrowthRepository {

    suspend fun getGrowthResult(): Result<GrowthResult>
}
