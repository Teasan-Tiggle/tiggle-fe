package com.ssafy.tiggle.presentation.ui.piggybank

import com.ssafy.tiggle.domain.entity.piggybank.AccountHolder
import com.ssafy.tiggle.domain.entity.piggybank.RegisterAccount

data class RegisterAccountState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val registerAccountStep: RegisterAccountStep = RegisterAccountStep.ACCOUNT,
    val registerAccount: RegisterAccount = RegisterAccount(),

    //예금주 정보
    val accountHolder: AccountHolder= AccountHolder()
)