package com.ssafy.tiggle.data.model.donation

import kotlinx.serialization.Serializable

@Serializable
data class DonationRankDto(
    val rank: Int,
    val name: String,
    val amount: Int
)
