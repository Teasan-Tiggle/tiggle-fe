package com.ssafy.tiggle.data.model.piggybank.request

data class VerifySMSRequestDto(
    val phone: String = "",
    val code: String = "",
    val purpose: String = ""
)