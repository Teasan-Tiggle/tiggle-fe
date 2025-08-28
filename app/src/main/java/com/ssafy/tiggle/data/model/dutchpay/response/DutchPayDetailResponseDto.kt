package com.ssafy.tiggle.data.model.dutchpay.response

import kotlinx.serialization.Serializable

@Serializable
data class DutchPayDetailResponseDto(
    val id: Long,
    val title: String,
    val message: String,
    val totalAmount: Int,
    val status: String,
    val creator: CreatorDto,
    val shares: List<ShareDto>,
    val roundedPerPerson: Int?,
    val payMore: Boolean,
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
    val amount: Int,
    val status: String
)
