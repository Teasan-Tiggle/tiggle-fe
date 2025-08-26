package com.ssafy.tiggle.data.model.donation

import kotlinx.serialization.Serializable

@Serializable
data class DonationRequestDto(
    val category: String,
    val amount: Int
)
