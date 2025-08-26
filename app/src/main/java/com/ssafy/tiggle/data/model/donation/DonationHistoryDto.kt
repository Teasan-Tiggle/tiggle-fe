package com.ssafy.tiggle.data.model.donation

import kotlinx.serialization.Serializable

@Serializable
data class DonationHistoryDto(
    val category: String,
    val donatedAt: String,
    val amount: Int,
    val title: String
)
