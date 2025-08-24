package com.ssafy.tiggle.data.model.piggybank.request

data class CreatePiggyBankRequestDto(
    val name: String = "",
    val targetAmount: Long = 0L,
    val esgCategoryId: Int = 1
)