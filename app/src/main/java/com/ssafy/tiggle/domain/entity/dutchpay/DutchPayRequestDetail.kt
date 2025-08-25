package com.ssafy.tiggle.domain.entity.dutchpay

data class DutchPayRequestDetail(
    val dutchPayId: Long,
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
    val isCreator: Boolean
)
