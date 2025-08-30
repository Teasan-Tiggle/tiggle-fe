package com.ssafy.tiggle.domain.entity.growth

data class GrowthResult(
    val totalAmount: Long = 0L,
    val experiencePoints: Int = 0,
    val toNextLevel: Int = 0,
    val level: Int = 0,
    val heart: Int = 0
)