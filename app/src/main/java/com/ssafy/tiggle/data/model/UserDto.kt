package com.ssafy.tiggle.data.model

import com.ssafy.tiggle.domain.entity.auth.User

/**
 * 사용자 DTO (Data Transfer Object)
 *
 * API 응답으로 받는 사용자 데이터 구조
 */
data class UserDto(
    val id: Long,
    val email: String,
    val name: String,
    val school: String,
    val department: String,
    val studentId: String,
    val createdAt: String? = null,
    val updatedAt: String? = null
) {
    /**
     * DTO를 도메인 엔티티로 변환
     */
    fun toDomain(): User {
        return User(
            id = id,
            email = email,
            name = name,
            school = school,
            department = department,
            studentId = studentId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}


