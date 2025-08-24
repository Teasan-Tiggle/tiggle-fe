package com.ssafy.tiggle.data.model.piggybank.response

data class CreatePiggyBankResponseDto(
    val name: String = "",
    val currentAmount: Long = 0L,
    val lastWeekSavedAmount: Int = 0
)