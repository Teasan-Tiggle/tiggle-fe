package com.ssafy.tiggle.data.model.auth.request

/**
 * 회원가입 요청 DTO
 */
data class SignUpRequestDto(
    val email: String,
    val name: String,
    val universityId: String,
    val departmentId: String,
    val studentId: String,
    val password: String,
    val phone: String
)