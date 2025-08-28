package com.ssafy.tiggle.data.model.dutchpay.response

import kotlinx.serialization.Serializable

@Serializable
data class DutchPaySummaryResponseDto(
    val totalTransferredAmount: Int,
    val transferCount: Int,
    val participatedCount: Int
)
