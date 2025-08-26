package com.ssafy.tiggle.data.model.donation

import kotlinx.serialization.Serializable

@Serializable
data class DonationAccountDto(
    val accountName: String,
    val accountNo: String,
    val balance: String
)
