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
