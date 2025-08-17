package com.ssafy.tiggle.domain.entity

/**
 * User 도메인 엔티티
 * 비즈니스 로직의 핵심 객체로, 외부 의존성이 없는 순수한 Kotlin 클래스
 */
data class User(
    val id: Long,
    val name: String,
    val email: String,
    val profileImageUrl: String? = null,
    val createdAt: Long,
    val updatedAt: Long
)
