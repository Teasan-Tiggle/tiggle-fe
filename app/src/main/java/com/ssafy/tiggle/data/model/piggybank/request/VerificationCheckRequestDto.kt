package com.ssafy.tiggle.data.model.piggybank.request

data class VerificationCheckRequestDto(
    val accountNo: String,
    val authCode: String
)