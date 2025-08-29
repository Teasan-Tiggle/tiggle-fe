package com.ssafy.tiggle.data.model.dutchpay.response

import kotlinx.serialization.Serializable
@Serializable
data class DutchPayRequestDetailResponseDto(
    val id: Long,
    val requestUserId: Long,
    val title: String,
    val message: String,
    val totalAmount: Long,
    val status: String,
    val creator: CreatorDto,
    val shares: List<ShareDto>,
    val roundedPerPerson: Long,
    val createdAt: String
)
@Serializable
data class CreatorDto(
    val id: Long,
    val name: String
)

@Serializable
data class ShareDto(
    val userId: Long,
    val name: String,
    val amount: Long,
    val status: String,
    val tiggleAmount: Long?
)