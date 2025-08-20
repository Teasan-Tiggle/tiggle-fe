package com.ssafy.tiggle.data.model

/**
 * 회원가입 요청 DTO
 */
data class SignUpRequestDto(
    val email: String,
    val password: String,
    val name: String,
    val school: String,
    val department: String,
    val studentId: String
)
