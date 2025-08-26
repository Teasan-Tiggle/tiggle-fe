package com.ssafy.tiggle.data.model.dutchpay.response

data class DutchPayRequestDetailResponseDto(
    val dutchpayId: Long,
    val title: String,
    val message: String,
    val requesterName: String,
    val participantCount: Int,
    val totalAmount: Long,
    val requestedAt: String,
    val myAmount: Long,
    val originalAmount: Long,
    val tiggleAmount: Long,
    val payMoreDefault: Boolean,
    val creator: Boolean
)