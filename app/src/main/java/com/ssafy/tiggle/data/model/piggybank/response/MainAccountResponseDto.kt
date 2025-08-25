package com.ssafy.tiggle.data.model.piggybank.response

import com.ssafy.tiggle.domain.entity.piggybank.AccountHolder
import com.ssafy.tiggle.domain.entity.piggybank.MainAccount

data class MainAccountResponseDto(
    val accountName:String="",
    val accountNo:String="",
    val balance:String=""
)

fun MainAccountResponseDto.toDomain(): MainAccount =
    MainAccount(
        accountName = accountName,
        accountNo= accountNo,
        balance=balance
    )
