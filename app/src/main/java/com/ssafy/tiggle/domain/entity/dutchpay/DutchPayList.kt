package com.ssafy.tiggle.domain.entity.dutchpay

data class DutchPayList(
    val items: List<DutchPayItem>,
    val nextCursor: String?,
    val hasNext: Boolean
)

