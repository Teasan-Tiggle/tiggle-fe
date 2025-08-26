package com.ssafy.tiggle.data.model.donation

data class DonationSummaryDto(
    val totalAmount: Int,
    val monthlyAmount: Int,
    val categoryCnt: Int,
    val universityRank: Int
)
