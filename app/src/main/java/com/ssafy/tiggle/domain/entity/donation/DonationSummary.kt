package com.ssafy.tiggle.domain.entity.donation

data class DonationSummary(
    val totalAmount: Int,
    val monthlyAmount: Int,
    val categoryCnt: Int,
    val universityRank: Int
)
