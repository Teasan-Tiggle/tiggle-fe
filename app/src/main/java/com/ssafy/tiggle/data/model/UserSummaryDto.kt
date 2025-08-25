package com.ssafy.tiggle.data.model

import com.ssafy.tiggle.domain.entity.dutchpay.UserSummary

/**
 * 사용자 요약 DTO (리스트용)
 */
data class UserSummaryDto(
    val id: Long,
    val name: String
) {
    fun toDomain(): UserSummary = UserSummary(
        id = id,
        name = name
    )
}


