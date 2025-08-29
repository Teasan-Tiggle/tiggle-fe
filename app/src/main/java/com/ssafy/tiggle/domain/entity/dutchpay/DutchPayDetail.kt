package com.ssafy.tiggle.domain.entity.dutchpay

data class DutchPayDetail(
    val id: Long,
    val title: String,
    val message: String,
    val totalAmount: Int,
    val status: String,
    val creator: Creator,
    val shares: List<Share>,
    val roundedPerPerson: Int?,
    val payMore: Boolean,
    val createdAt: String
)

data class Creator(
    val id: Long,
    val name: String
)

data class Share(
    val userId: Long,
    val name: String,
    val amount: Int,
    val tiggleAmount: Int? = null,
    val status: String
)
