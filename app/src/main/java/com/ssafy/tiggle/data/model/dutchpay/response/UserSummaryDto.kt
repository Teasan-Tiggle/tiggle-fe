package com.ssafy.tiggle.data.model.dutchpay.response

import com.ssafy.tiggle.domain.entity.dutchpay.UserSummary

/**
 * 사용자 요약 DTO (리스트용)
 */
data class UserSummaryDto(
    val id: Long,
    val name: String,
    val university: String? = null,
    val department: String? = null
) {
    fun toDomain(): UserSummary = UserSummary(
        id = id,
        name = name,
        university = university,
        department = department
    )
}