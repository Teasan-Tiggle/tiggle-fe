package com.ssafy.tiggle.domain.entity.dutchpay

/**
 * 사용자 요약 도메인 엔티티 (리스트/피커용)
 */
data class UserSummary(
    val id: Long,
    val name: String,
    val university: String? = null,
    val department: String? = null
)