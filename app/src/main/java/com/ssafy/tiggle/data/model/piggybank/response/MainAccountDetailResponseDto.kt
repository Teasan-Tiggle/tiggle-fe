package com.ssafy.tiggle.data.model.piggybank.response

import com.ssafy.tiggle.domain.entity.piggybank.DomainTransaction
import com.ssafy.tiggle.domain.entity.piggybank.MainAccountDetail

data class MainAccountDetailResponseDto(
    val transactions: List<Transaction>,
    val nextCursor: String?,
    val hasNext: Boolean,
    val size: Int
)

data class Transaction(
    val transactionId: String,
    val transactionDate: String,
    val transactionTime: String,
    val transactionType: String,
    val description: String,
    val amount: Int,
    val balanceAfter: Int
)

fun MainAccountDetailResponseDto.toDomain(): MainAccountDetail =
    MainAccountDetail(
        transactions = transactions.map { it.toDomain() },
        nextCursor = nextCursor,
        hasNext = hasNext,
        size = size
    )

fun Transaction.toDomain(): DomainTransaction =
    DomainTransaction(
        transactionId = transactionId,
        transactionDate = transactionDate,
        transactionTime = transactionTime,
        transactionType = transactionType,
        description = description,
        amount = amount,
        balanceAfter = balanceAfter
    )