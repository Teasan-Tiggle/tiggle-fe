package com.ssafy.tiggle.domain.entity.piggybank

data class PiggyBank(
    val id: Int = 0,
    val name: String = "",
    val currentAmount: Long = 0L,
    val targetAmount: Long = 0L,
    val savingCount: Int = 0,
    val donationCount: Int = 0,
    val donationTotalAmount: Long = 0L,
    val autoDonation: Boolean = false,
    val autoSaving: Boolean = false,
    val esgCategory: EsgCategory? = null,
)

data class EsgCategory(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val characterName: String = ""
)
