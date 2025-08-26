package com.ssafy.tiggle.presentation.ui.piggybank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.entity.piggybank.ValidationField
import com.ssafy.tiggle.domain.usecase.piggybank.PiggyBankUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OpenAccountViewModel @Inject constructor(
    val useCases: PiggyBankUseCases
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
            piggyBankAccount = _uiState.value.piggyBankAccount.copy(
                targetDonationAmount = amount.toLongOrNull() ?: 0,
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
        val digits = code.filter(Char::isDigit).take(6)
        val error = _uiState.value.piggyBankAccount.validateCode(digits)

        _uiState.value = _uiState.value.copy(
            piggyBankAccount = _uiState.value.piggyBankAccount.copy(
                certificateCode = digits,
                codeError = error
            )
        )
    }

    fun updatePhoneNum(phoneNum: String) {
        val validated = _uiState.value.piggyBankAccount
            .copy(phoneNum = phoneNum)
            .validateField(ValidationField.PHONE)

        _uiState.value = _uiState.value.copy(piggyBankAccount = validated)

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
                val validated = _uiState.value.piggyBankAccount
                    .validateField(ValidationField.PHONE)
                _uiState.value = _uiState.value.copy(piggyBankAccount = validated)

                if (validated.phoneNumError != null) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = validated.phoneNumError
                    )
                    false
                } else {
                    true
                }
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


    fun createPiggyBank() {
        val name = _uiState.value.piggyBankAccount.piggyBankName
        val targetAmount = _uiState.value.piggyBankAccount.targetDonationAmount

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = useCases.createPiggyBankUseCase(name, targetAmount, 1)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                goToNextStep()
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "계좌개설 정보 보내기에 실패했습니다."
                    )
                }
            }
        }
    }

    private fun resetWhenExhausted(msg: String) {
        _uiState.update {
            it.copy(
                openAccountStep = OpenAccountStep.INFO,
                errorMessage = msg,
                piggyBankAccount = it.piggyBankAccount.copy(
                    certificateCode = "",
                    codeError = null,
                    attemptsLeft = 3
                )
            )
        }
    }

    fun sendSMS() {
        val phoneNum = _uiState.value.piggyBankAccount.phoneNum

        // 1) 로컬 유효성 먼저 체크 (11자리 숫자)
        val phoneErr = _uiState.value.piggyBankAccount.validatePhoneNum(phoneNum)
        if (phoneErr != null) {
            _uiState.update {
                it.copy(
                    piggyBankAccount = it.piggyBankAccount.copy(phoneNumError = phoneErr),
                    errorMessage = phoneErr
                )
            }
            return
        }

        // 2) 전송 가능 횟수 체크
        val left = _uiState.value.piggyBankAccount.attemptsLeft
        if (left <= 0) {
            resetWhenExhausted("인증번호 전송 가능 횟수를 모두 사용했어요. 처음 화면으로 돌아갑니다.")
            return
        }

        // 3) 즉시 CODE 화면으로 이동
        _uiState.update {
            it.copy(
                openAccountStep = OpenAccountStep.CODE,
                isLoading = true,
                errorMessage = null,
                piggyBankAccount = it.piggyBankAccount.copy(
                    certificateCode = "",
                    codeError = null
                )
            )
        }

        // 4) SMS 전송은 비동기로 진행하면서 시도 횟수 차감
        viewModelScope.launch {
            val result = useCases.sendSMSUseCase(phoneNum, "account_opening")
            result.onSuccess {
                _uiState.update { st ->
                    st.copy(
                        isLoading = false,
                        piggyBankAccount = st.piggyBankAccount.copy(
                            attemptsLeft = (st.piggyBankAccount.attemptsLeft - 1).coerceAtLeast(0)
                        )
                    )
                }
            }.onFailure { e ->
                _uiState.update { st ->
                    val nextLeft = (st.piggyBankAccount.attemptsLeft - 1).coerceAtLeast(0)
                    st.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "인증번호 발송을 실패했습니다.",
                        piggyBankAccount = st.piggyBankAccount.copy(
                            attemptsLeft = nextLeft
                        )
                    )
                }
                if (_uiState.value.piggyBankAccount.attemptsLeft <= 0) {
                    resetWhenExhausted("인증번호 전송 가능 횟수를 모두 사용했어요. 처음 화면으로 돌아갑니다.")
                }
            }
        }
    }


    /** 인증코드 확인 → verificationToken 저장 → 주계좌 등록 → 성공화면 */
    fun verifySMS() {
        val phoneNum = _uiState.value.piggyBankAccount.phoneNum
        val code = _uiState.value.piggyBankAccount.certificateCode

        if (code.length != 6) {
            _uiState.update { it.copy(errorMessage = "인증번호 6자리를 입력해주세요.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = useCases.verifySMSUseCase(phoneNum, code, "account_opening")
            result.onSuccess { response ->
                if (response.match) {
                    // 일치하면 계좌 개설 요청
                    createPiggyBank()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "인증번호가 일치하지 않습니다.",
                            piggyBankAccount = it.piggyBankAccount.copy(
                                codeError = "인증번호가 일치하지 않습니다."
                            )
                        )
                    }
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "인증번호 인증에 실패했습니다."
                    )
                }
            }
        }
    }


    fun resendSMS() {
        val phoneNum = _uiState.value.piggyBankAccount.phoneNum
        val left = _uiState.value.piggyBankAccount.attemptsLeft
        if (left <= 0) {
            resetWhenExhausted("인증번호 전송 가능 횟수를 모두 사용했어요. 처음 화면으로 돌아갑니다.")
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = useCases.sendSMSUseCase(phoneNum, "account_opening")
            result.onSuccess {
                // ✅ 재전송은 같은 단계(CODE)에 머무름
                _uiState.update { st ->
                    st.copy(
                        isLoading = false,
                        piggyBankAccount = st.piggyBankAccount.copy(
                            attemptsLeft = (st.piggyBankAccount.attemptsLeft - 1).coerceAtLeast(0)
                        )
                    )
                }
            }.onFailure { e ->
                _uiState.update { st ->
                    val nextLeft = (st.piggyBankAccount.attemptsLeft - 1).coerceAtLeast(0)
                    st.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "인증번호 재전송에 실패했습니다.",
                        piggyBankAccount = st.piggyBankAccount.copy(attemptsLeft = nextLeft)
                    )
                }
                if (_uiState.value.piggyBankAccount.attemptsLeft <= 0) {
                    resetWhenExhausted("인증번호 전송 가능 횟수를 모두 사용했어요. 처음 화면으로 돌아갑니다.")
                }
            }
        }
    }

    private var mode: OpenAccountMode = OpenAccountMode.FULL
    fun setMode(m: OpenAccountMode) {
        mode = m
    }

    // 기존 goToNextStep는 그대로 두고,
    // INFO에서만 분기하는 전용 진입점 추가
    fun nextFromInfo() {
        if (mode == OpenAccountMode.SIMPLE) {
            //바로 SUCCESS
            _uiState.update { it.copy(openAccountStep = OpenAccountStep.SUCCESS) }
            return
        }
        //기존 단계 진행
        goToNextStep()
    }

    fun modifyPiggyBankInfo() {
        val name = _uiState.value.piggyBankAccount.piggyBankName
        val targetAmount = _uiState.value.piggyBankAccount.targetDonationAmount

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = useCases.setPiggyBankSettingUseCase(
                name = name,
                targetAmount = targetAmount
            )

            result.onSuccess { updated ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        openAccountStep = OpenAccountStep.SUCCESS,
                        errorMessage = null
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "저금통 정보 수정에 실패했습니다."
                    )
                }
            }
        }
    }

}