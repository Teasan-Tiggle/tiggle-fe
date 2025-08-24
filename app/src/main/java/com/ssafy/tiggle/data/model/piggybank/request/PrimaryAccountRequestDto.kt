package com.ssafy.tiggle.data.model.piggybank.request

data class PrimaryAccountRequestDto(
    val accountNo: String,
    val verificationToken: String
)