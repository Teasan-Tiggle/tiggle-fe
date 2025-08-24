package com.ssafy.tiggle.presentation.ui.dutchpay

import com.ssafy.tiggle.domain.entity.UserSummary

data class CreateDutchPayState(
    val step: CreateDutchPayStep = CreateDutchPayStep.PICK_USERS,
    val users: List<UserSummary> = emptyList(),
    val selectedUserIds: Set<Long> = emptySet(),
    val amountText: String = "",
    val payMore: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
