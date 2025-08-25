package com.ssafy.tiggle.data.model.dutchpay.request

data class DutchPayRequestDto(
    val userIds: List<Long>,
    val totalAmount: Long,
    val title: String,
    val message: String,
    val payMore: Boolean
)
