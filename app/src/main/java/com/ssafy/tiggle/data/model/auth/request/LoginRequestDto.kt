package com.ssafy.tiggle.data.model.auth.request

data class LoginRequestDto(
    val email: String,
    val password: String
)