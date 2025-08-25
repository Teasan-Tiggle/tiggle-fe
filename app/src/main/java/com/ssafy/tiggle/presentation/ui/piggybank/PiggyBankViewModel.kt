package com.ssafy.tiggle.presentation.ui.piggybank

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PiggyBankViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        PiggyBankState(
            hasPiggyBank = true,
            hasLinkedAccount = true,
            todaySaving = 847,
            lastWeekRemainder = 15847,
            lastWeekRounded = 15000,
            accountBank = "신한",
            accountName = "쏠편한 입출금통장(저축예금)",
            accountNumberMasked = "123-456-789000",
            accountBalance = 100_000,
            changeLeftoverDonate = true,
            achieveGoalDonate = true


        )
    )
    val uiState: StateFlow<PiggyBankState> = _uiState.asStateFlow()

    fun setChangeLeftoverDonate(value: Boolean) {
        _uiState.update { it.copy(changeLeftoverDonate = value) }
    }

    fun setAchieveGoalDonate(value: Boolean) {
        _uiState.update { it.copy(achieveGoalDonate = value) }
    }

    fun setHasPiggyBank(v: Boolean) {
        _uiState.update { it.copy(hasPiggyBank = v) }
    }

    fun setHasLinkedAccount(v: Boolean) {
        _uiState.update { it.copy(hasLinkedAccount = v) }
    }
}