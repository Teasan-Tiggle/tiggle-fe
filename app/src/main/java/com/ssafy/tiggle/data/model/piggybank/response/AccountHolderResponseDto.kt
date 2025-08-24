package com.ssafy.tiggle.data.model.piggybank.response

import com.ssafy.tiggle.domain.entity.piggybank.AccountHolder

data class AccountHolderResponseDto(
    val bankName: String,
    val accountNo: String,
    val userName: String
)

fun AccountHolderResponseDto.toDomain(): AccountHolder =
    AccountHolder(
        bankName = bankName,
        accountNo = accountNo,
        userName = userName
    )
