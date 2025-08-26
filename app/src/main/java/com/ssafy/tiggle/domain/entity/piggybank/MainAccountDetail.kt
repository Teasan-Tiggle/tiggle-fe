package com.ssafy.tiggle.domain.entity.piggybank

data class MainAccountDetail(
    val transactions: List<DomainTransaction> = emptyList(),
    val nextCursor: String? = null,
    val hasNext: Boolean = false,
    val size: Int = 0
)

data class DomainTransaction(
    val transactionId: String = "",
    val transactionDate: String = "",
    val transactionTime: String = "",
    val transactionType: String = "",
    val description: String = "",
    val amount: Int = 0,
    val balanceAfter: Int = 0
)
