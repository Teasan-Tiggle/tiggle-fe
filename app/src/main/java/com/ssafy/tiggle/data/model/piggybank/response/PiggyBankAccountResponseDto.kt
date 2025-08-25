package com.ssafy.tiggle.data.model.piggybank.response

import com.ssafy.tiggle.domain.entity.piggybank.PiggyBankAccount

data class PiggyBankAccountResponseDto (
    val name:String="",
    val currentAmount:Long=0L,
    val lastWeekSavedAmount:Long=0L
)

fun PiggyBankAccountResponseDto.toDomain():PiggyBankAccount=
    PiggyBankAccount(
        name=name,
        currentAmount=currentAmount,
        lastWeekSavedAmount=lastWeekSavedAmount
    )