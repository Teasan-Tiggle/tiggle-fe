package com.ssafy.tiggle.presentation.ui.dutchpay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequest
import com.ssafy.tiggle.domain.usecase.dutchpay.GetAllUsersUseCase
import com.ssafy.tiggle.domain.usecase.dutchpay.CreateDutchPayRequestUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateDutchPayViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val createDutchPayRequestUseCase: CreateDutchPayRequestUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateDutchPayState())
    val uiState: StateFlow<CreateDutchPayState> = _uiState.asStateFlow()

    fun loadUsers() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            getAllUsersUseCase()
                .onSuccess { list ->
                    _uiState.update { it.copy(users = list, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                }
        }
    }

    fun toggleUser(userId: Long) {
        _uiState.update { current ->
            val next = current.selectedUserIds.toMutableSet().apply {
                if (contains(userId)) remove(userId) else add(userId)
            }
            current.copy(selectedUserIds = next)
        }
    }

    fun updateAmount(text: String) {
        _uiState.update { it.copy(amountText = text.filter { ch -> ch.isDigit() }) }
    }

    fun setPayMore(value: Boolean) {
        _uiState.update { it.copy(payMore = value) }
    }

    fun updateTitle(text: String) {
        _uiState.update { it.copy(title = text) }
    }

    fun updateMessage(text: String) {
        _uiState.update { it.copy(message = text) }
    }

    fun goNext() {
        _uiState.update { current ->
            when (current.step) {
                CreateDutchPayStep.PICK_USERS -> {
                    current.copy(step = CreateDutchPayStep.INPUT_AMOUNT)
                }

                CreateDutchPayStep.INPUT_AMOUNT -> {
                    // 더치페이 요청 API 호출
                    createDutchPayRequest()
                    current
                }

                CreateDutchPayStep.COMPLETE -> current
            }
        }
    }

    private fun createDutchPayRequest() {
        val currentState = _uiState.value

        // 입력값 검증
        if (currentState.selectedUserIds.isEmpty() ||
            currentState.amountText.isBlank() ||
            currentState.title.isBlank()
        ) {
            _uiState.update { it.copy(errorMessage = "필수 입력값을 확인해주세요.") }
            return
        }

        val totalAmount = currentState.amountText.toLongOrNull()
        if (totalAmount == null || totalAmount <= 0) {
            _uiState.update { it.copy(errorMessage = "올바른 금액을 입력해주세요.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val request = DutchPayRequest(
                userIds = currentState.selectedUserIds.toList(),
                totalAmount = totalAmount,
                title = currentState.title,
                message = currentState.message,
                payMore = currentState.payMore
            )

            createDutchPayRequestUseCase(request)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            step = CreateDutchPayStep.COMPLETE,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "더치페이 요청에 실패했습니다."
                        )
                    }
                }
        }
    }

    fun goPrev() {
        _uiState.update { current ->
            val prev = when (current.step) {
                CreateDutchPayStep.PICK_USERS -> CreateDutchPayStep.PICK_USERS
                CreateDutchPayStep.INPUT_AMOUNT -> CreateDutchPayStep.PICK_USERS
                CreateDutchPayStep.COMPLETE -> CreateDutchPayStep.INPUT_AMOUNT
            }
            current.copy(step = prev)
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
