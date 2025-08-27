package com.ssafy.tiggle.presentation.ui.dutchpay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequest
import com.ssafy.tiggle.domain.usecase.dutchpay.CreateDutchPayRequestUseCase
import com.ssafy.tiggle.domain.usecase.dutchpay.GetAllUsersUseCase
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
        val currentState = _uiState.value
        if (currentState.isLoading) {
            return // 중복 실행 방지
        }

        // when 문을 update 블록 밖으로 꺼냅니다.
        when (currentState.step) {
            CreateDutchPayStep.PICK_USERS -> {
                // 상태 변경만 필요하므로 update 블록 사용
                _uiState.update { it.copy(step = CreateDutchPayStep.INPUT_AMOUNT) }
            }

            CreateDutchPayStep.INPUT_AMOUNT -> {
                // API 요청 함수를 직접 호출
                createDutchPayRequest()
            }

            CreateDutchPayStep.COMPLETE -> {
                // 이 로직은 UI에서 onFinish 콜백으로 처리되므로 ViewModel에서는 할 일 없음
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
