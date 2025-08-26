package com.ssafy.tiggle.domain.entity.piggybank

data class PiggyBankEntry(
    val id: String = "",
    val type: String = "",
    val amount: Long = 0L,
    val occurredAt: String = "",
    val title: String = ""
)
