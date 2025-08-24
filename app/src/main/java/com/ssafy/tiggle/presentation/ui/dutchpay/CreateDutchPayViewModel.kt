package com.ssafy.tiggle.presentation.ui.dutchpay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.usecase.GetAllUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CreateDutchPayViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase
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

    fun goNext() {
        _uiState.update { current ->
            val next = when (current.step) {
                CreateDutchPayStep.PICK_USERS -> CreateDutchPayStep.INPUT_AMOUNT
                CreateDutchPayStep.INPUT_AMOUNT -> CreateDutchPayStep.COMPLETE
                CreateDutchPayStep.COMPLETE -> CreateDutchPayStep.COMPLETE
            }
            current.copy(step = next)
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
}
