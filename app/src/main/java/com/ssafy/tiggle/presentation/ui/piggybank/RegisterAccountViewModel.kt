package com.ssafy.tiggle.presentation.ui.piggybank

import androidx.lifecycle.ViewModel
import com.ssafy.tiggle.domain.entity.account.ValidationRegisterField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RegisterAccountViewModel @Inject constructor(
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterAccountState())
    val uiState: StateFlow<RegisterAccountState> = _uiState.asStateFlow()

    //단계 이동
    fun goToNextStep(): Boolean {
        val currentStep = _uiState.value.registerAccountStep
        val canProceed = validateCurrentStep()

        if (canProceed) {
            val nextStep = when (currentStep) {
                RegisterAccountStep.ACCOUNT -> RegisterAccountStep.ACCOUNTSUCCESS
                RegisterAccountStep.ACCOUNTSUCCESS -> RegisterAccountStep.SENDCODE
                RegisterAccountStep.SENDCODE -> RegisterAccountStep.CERTIFICATION
                RegisterAccountStep.CERTIFICATION -> RegisterAccountStep.SUCCESS
                RegisterAccountStep.SUCCESS -> RegisterAccountStep.SUCCESS
            }

            _uiState.value = _uiState.value.copy(registerAccountStep = nextStep)
        }

        return canProceed
    }

    private fun validateCurrentStep(): Boolean {
        val currentState = _uiState.value

        return when (currentState.registerAccountStep) {
            RegisterAccountStep.ACCOUNT -> {
                val validated = _uiState.value.registerAccount
                    .validateField(ValidationRegisterField.ACCOUNT)
                _uiState.value = _uiState.value.copy(registerAccount = validated)

                if (validated.accountNumError != null) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = validated.accountNumError
                    )
                    false
                }
                true
            }

            RegisterAccountStep.ACCOUNTSUCCESS -> {
                true
            }

            RegisterAccountStep.SENDCODE -> {
                true
            }

            RegisterAccountStep.CERTIFICATION -> {
                true
            }

            RegisterAccountStep.SUCCESS -> {
                true
            }
        }
    }

    fun goToPreviousStep() {
        val currentStep = _uiState.value.registerAccountStep
        val previousStep = when (currentStep) {
            RegisterAccountStep.ACCOUNT -> RegisterAccountStep.ACCOUNT
            RegisterAccountStep.ACCOUNTSUCCESS -> RegisterAccountStep.ACCOUNT
            RegisterAccountStep.SENDCODE -> RegisterAccountStep.ACCOUNTSUCCESS
            RegisterAccountStep.CERTIFICATION -> RegisterAccountStep.SENDCODE
            RegisterAccountStep.SUCCESS -> RegisterAccountStep.CERTIFICATION
        }

        _uiState.value = _uiState.value.copy(registerAccountStep = previousStep)
    }

    // 사용자 데이터 업데이트 (도메인 엔티티의 유효성 검사 사용)
    fun updateAccountNum(accountNum: String) {
        val currentData = _uiState.value.registerAccount
        val newData = currentData.copy(accountNum = accountNum)
            .validateField(ValidationRegisterField.ACCOUNT)
        _uiState.value = _uiState.value.copy(registerAccount = newData)
    }
    fun updateCode(code: String) {
        val error = _uiState.value.registerAccount.validateCode(code)
        _uiState.value = _uiState.value.copy(
            registerAccount = _uiState.value.registerAccount.copy(
                code = code.toIntOrNull() ?: 0,
                codeError = error
            )
        )
    }

}