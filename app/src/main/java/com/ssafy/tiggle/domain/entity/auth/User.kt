package com.ssafy.tiggle.domain.entity.auth

/**
 * 사용자 도메인 엔티티
 */
data class User(
    val id: Long,
    val email: String,
    val name: String,
    val school: String,
    val department: String,
    val studentId: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
)