package com.ssafy.tiggle.domain.entity.dutchpay

data class DutchPayRequest(
    val userIds: List<Long>,
    val totalAmount: Long,
    val title: String,
    val message: String,
    val payMore: Boolean
)
