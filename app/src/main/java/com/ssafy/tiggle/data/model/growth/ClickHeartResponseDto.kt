package com.ssafy.tiggle.data.model.growth

import com.ssafy.tiggle.domain.entity.growth.HeartResult

data class ClickHeartResponseDto(
    val experiencePoints: Int = 0,
    val toNextLevel: Int = 0,
    val level: Int = 0,
    val heart: Int = 0
)

fun ClickHeartResponseDto.toDomain(): HeartResult =
    HeartResult(
        experiencePoints = experiencePoints,
        toNextLevel = toNextLevel,
        level = level,
        heart = heart
    )