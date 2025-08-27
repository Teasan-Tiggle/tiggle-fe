package com.ssafy.tiggle.data.model.growth

import com.ssafy.tiggle.domain.entity.growth.GrowthResult

data class GrowthResponseDto (
    val totalAmount:Long=0L,
    val toNextLevel:Int=0,
    val level:Int=0
)

fun GrowthResponseDto.toDomain(): GrowthResult =
    GrowthResult(
        totalAmount=totalAmount,
        toNextLevel=toNextLevel,
        level=level
    )