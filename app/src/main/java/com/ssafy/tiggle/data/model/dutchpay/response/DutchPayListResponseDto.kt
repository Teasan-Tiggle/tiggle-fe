package com.ssafy.tiggle.data.model.dutchpay.response

import kotlinx.serialization.Serializable

@Serializable
data class DutchPayListResponseDto(
    val items: List<DutchPayItemDto>,
    val nextCursor: String?,
    val hasNext: Boolean
)

@Serializable
data class DutchPayItemDto(
    val dutchpayId: Long,
    val title: String,
    val myAmount: Int,
    val totalAmount: Int,
    val participantCount: Int,
    val paidCount: Int,
    val requestedAt: String,
    val isCreator: Boolean
)
