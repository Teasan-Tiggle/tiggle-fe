package com.ssafy.tiggle.presentation.ui.piggybank

import androidx.lifecycle.ViewModel
import com.ssafy.tiggle.domain.entity.account.ValidationField
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class OpenAccountViewModel @Inject constructor(

) : ViewModel() {
    private val _uiState = MutableStateFlow(OpenAccountState())
    val uiState: StateFlow<OpenAccountState> = _uiState.asStateFlow()

    // 단계 이동
    fun goToNextStep(): Boolean {
        val currentStep = _uiState.value.openAccountStep
        val canProceed = validateCurrentStep()

        if (canProceed) {
            val nextStep = when (currentStep) {
                OpenAccountStep.INFO -> OpenAccountStep.TERMS
                OpenAccountStep.TERMS -> OpenAccountStep.CERTIFICATION
                OpenAccountStep.CERTIFICATION -> OpenAccountStep.CODE
                OpenAccountStep.CODE -> OpenAccountStep.SUCCESS
                OpenAccountStep.SUCCESS -> OpenAccountStep.SUCCESS
            }

            _uiState.value = _uiState.value.copy(openAccountStep = nextStep)
        }

        return canProceed
    }

    // 약관 동의 관련
    fun updateTermsAgreement(type: TermsType, isChecked: Boolean) {
        val currentTerms = _uiState.value.termsData
        val newTerms = when (type) {
            TermsType.SERVICE -> currentTerms.copy(serviceTerms = isChecked)
            TermsType.PRIVACY -> currentTerms.copy(privacyPolicy = isChecked)
            TermsType.MARKETING -> currentTerms.copy(marketingOptional = isChecked)
            TermsType.FINANCE -> currentTerms.copy(financeTerms = isChecked)
            TermsType.ALL -> currentTerms.copy(
                serviceTerms = isChecked,
                privacyPolicy = isChecked,
                marketingOptional = isChecked,
                financeTerms = isChecked
            )
        }

        _uiState.value = _uiState.value.copy(termsData = newTerms)
    }

    fun updateTargetDonationAmount(amount: String) {
        val error = _uiState.value.piggyBankAccount.validateTargetDonationAmount(amount)

        _uiState.value = _uiState.value.copy(
            amountInput = amount,
            piggyBankAccount = _uiState.value.piggyBankAccount.copy(
                targetDonationAmount = amount.toIntOrNull() ?: 0,
                amountError = error
            )
        )
    }

    // 사용자 데이터 업데이트 (도메인 엔티티의 유효성 검사 사용)
    fun updatePiggyBankName(piggyBankName: String) {
        val currentData = _uiState.value.piggyBankAccount
        val newData = currentData.copy(piggyBankName = piggyBankName)
            .validateField(ValidationField.ACCOUNTNAME)
        _uiState.value = _uiState.value.copy(piggyBankAccount = newData)
    }

    fun updateCode(code: String) {
        val error = _uiState.value.piggyBankAccount.validateCode(code)
        _uiState.value = _uiState.value.copy(
            piggyBankAccount = _uiState.value.piggyBankAccount.copy(
                certificateCode = code.toIntOrNull() ?: 0,
                codeError = error
            )
        )
    }

    fun goToPreviousStep() {
        val currentStep = _uiState.value.openAccountStep
        val previousStep = when (currentStep) {
            OpenAccountStep.INFO -> OpenAccountStep.INFO
            OpenAccountStep.TERMS -> OpenAccountStep.INFO
            OpenAccountStep.CERTIFICATION -> OpenAccountStep.TERMS
            OpenAccountStep.CODE -> OpenAccountStep.CERTIFICATION
            OpenAccountStep.SUCCESS -> OpenAccountStep.CODE
        }

        _uiState.value = _uiState.value.copy(openAccountStep = previousStep)
    }

    private fun validateCurrentStep(): Boolean {
        val currentState = _uiState.value

        return when (currentState.openAccountStep) {
            OpenAccountStep.INFO -> {
                val validated = _uiState.value.piggyBankAccount
                    .validateField(ValidationField.AMOUNT)
                    .validateField(ValidationField.ACCOUNTNAME)
                _uiState.value = _uiState.value.copy(piggyBankAccount = validated)

                val ok = validated.amountError == null && validated.piggyBankNameError == null
                if (!ok) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = validated.amountError ?: validated.piggyBankNameError
                    )
                }
                ok
            }

            OpenAccountStep.TERMS -> {
                if (!currentState.termsData.allRequired) {
                    _uiState.value = currentState.copy(
                        errorMessage = "필수 약관에 동의해주세요."
                    )
                    false
                } else {
                    true
                }
            }

            OpenAccountStep.CERTIFICATION -> {
                true
            }

            OpenAccountStep.CODE -> {
                val validated = _uiState.value.piggyBankAccount
                    .validateField(ValidationField.CODE)
                _uiState.value = _uiState.value.copy(piggyBankAccount = validated)

                if (validated.codeError != null) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = validated.codeError
                    )
                    false
                } else {
                    true
                }
            }

            OpenAccountStep.SUCCESS -> {
                true
            }
        }
    }


}