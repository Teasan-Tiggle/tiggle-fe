package com.ssafy.tiggle.domain.entity.dutchpay

data class DutchPayItem(
    val dutchpayId: Long,
    val title: String,
    val myAmount: Int,
    val totalAmount: Int,
    val participantCount: Int,
    val paidCount: Int,
    val requestedAt: String,
    val isCreator: Boolean,
    val creatorName: String,
    val tiggleAmount: Int
)