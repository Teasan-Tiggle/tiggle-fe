package com.ssafy.tiggle.presentation.ui.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.entity.donation.DonationCategory
import com.ssafy.tiggle.domain.entity.donation.DonationRequest
import com.ssafy.tiggle.domain.usecase.donation.CreateDonationUseCase
import com.ssafy.tiggle.domain.usecase.donation.GetDonationAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DonationModalViewModel @Inject constructor(
    private val getDonationAccountUseCase: GetDonationAccountUseCase,
    private val createDonationUseCase: CreateDonationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonationModalUiState())
    val uiState: StateFlow<DonationModalUiState> = _uiState.asStateFlow()

    init {
        loadAccount()
    }

    private fun loadAccount() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val result = getDonationAccountUseCase()
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    account = result.getOrNull()
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "계좌 정보를 불러올 수 없습니다."
                )
            }
        }
    }

    fun onCategorySelected(category: DonationCategory) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun onAmountChanged(amount: String) {
        // 숫자만 입력 가능하도록 필터링
        val filteredAmount = amount.filter { it.isDigit() }
        _uiState.value = _uiState.value.copy(amount = filteredAmount)
    }

    fun createDonation() {
        val amount = _uiState.value.amount.toIntOrNull()
        if (amount == null || amount <= 0) {
            _uiState.value = _uiState.value.copy(errorMessage = "올바른 금액을 입력해주세요.")
            return
        }

        val account = _uiState.value.account
        if (account == null) {
            _uiState.value = _uiState.value.copy(errorMessage = "계좌 정보를 불러올 수 없습니다.")
            return
        }

        val accountBalance = account.balance.toIntOrNull() ?: 0
        if (amount > accountBalance) {
            _uiState.value = _uiState.value.copy(errorMessage = "잔액이 부족합니다.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val request = DonationRequest(
                category = _uiState.value.selectedCategory,
                amount = amount
            )
            
            val result = createDonationUseCase(request)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isSuccess = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = result.exceptionOrNull()?.message ?: "기부 처리 중 오류가 발생했습니다."
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = DonationModalUiState()
        loadAccount()
    }
}
