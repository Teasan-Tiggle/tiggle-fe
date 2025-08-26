package com.ssafy.tiggle.domain.entity.donation

data class DonationRequest(
    val category: DonationCategory,
    val amount: Int
)
