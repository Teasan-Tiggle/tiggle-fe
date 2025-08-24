package com.ssafy.tiggle.data.model.piggybank.request

data class SendSMSRequestDto(
    val phone: String = "",
    val purpose: String = "",
)