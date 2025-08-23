package com.ssafy.tiggle.presentation.ui.piggybank

import com.ssafy.tiggle.domain.entity.account.RegisterAccount

data class RegisterAccountState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val registerAccountStep: RegisterAccountStep = RegisterAccountStep.ACCOUNT,
    val registerAccount: RegisterAccount = RegisterAccount(),
)