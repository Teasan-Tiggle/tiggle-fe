package com.ssafy.tiggle.presentation.ui.piggybank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.entity.piggybank.RegisterAccount
import com.ssafy.tiggle.domain.entity.piggybank.ValidationRegisterField
import com.ssafy.tiggle.domain.usecase.piggybank.PiggyBankUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterAccountViewModel @Inject constructor(
    val piggyBankUseCases: PiggyBankUseCases
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

            else -> true
        }
    }

    // 뒤로가기 버튼 클릭시 사용
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

    //소유주 인증 실패시 오류 다이얼로그 닫기
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun fetchAccountHolder() {
        val accNo = _uiState.value.registerAccount.accountNum
        if (accNo.isBlank()) return

        // 로컬 유효성 먼저
        val current = _uiState.value.registerAccount
        val err = current.validateAccountNum(current.accountNum)
        if (err != null) {
            _uiState.value = _uiState.value.copy(errorMessage = err)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val result = piggyBankUseCases.getAccountHolderUseCase(accNo)
            _uiState.value = result.fold(
                onSuccess = { holder ->
                    _uiState.value.copy(
                        isLoading = false,
                        accountHolder = holder,
                        registerAccountStep = RegisterAccountStep.ACCOUNTSUCCESS
                    )
                },
                onFailure = { e ->
                    _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "계좌 확인에 실패했어요. 잠시 후 다시 시도해주세요."
                    )
                }
            )
        }
    }

    //1원 송금 요청
    fun requestOneWon() {
        val accountNo = _uiState.value.accountHolder.accountNo

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = piggyBankUseCases.requestOneWonVerificationUseCase(accountNo)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                goToNextStep()
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "1원 송금 요청에 실패했습니다."
                    )
                }
            }
        }
    }

    /** 인증코드 확인 → verificationToken 저장 → 주계좌 등록 → 성공화면 */
    fun confirmCodeAndRegisterPrimary() {
        val accountNo = _uiState.value.accountHolder.accountNo
        val code = _uiState.value.registerAccount.code

        if (code.length != 4) {
            _uiState.update { it.copy(errorMessage = "인증번호 4자리를 입력해주세요.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // 1) 인증 코드 검증 → 토큰 획득
            val check = piggyBankUseCases.requestOneWonCheckVerificationUseCase(accountNo, code)
            check.onSuccess { token ->
                // 상태에 저장
                _uiState.update {
                    it.copy(registerAccount = it.registerAccount.copy(verificationToken = token))
                }

                // 2) 주계좌 등록
                val reg = piggyBankUseCases.registerPrimaryAccountUseCase(accountNo, token)
                reg.onSuccess {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            registerAccountStep = RegisterAccountStep.SUCCESS
                        )
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "주계좌 등록에 실패했습니다."
                        )
                    }
                }
            }.onFailure { e ->
                // 인증 횟수 차감 처리
                val current = _uiState.value.registerAccount
                val left = (current.attemptsLeft - 1).coerceAtLeast(0)

                if (left <= 0) {
                    // 0번 남으면 처음으로 리셋
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "인증 실패 횟수를 초과했습니다. 처음부터 다시 시도해주세요.",
                            registerAccountStep = RegisterAccountStep.ACCOUNT,
                            registerAccount = RegisterAccount(), // 초기화
                            accountHolder = com.ssafy.tiggle.domain.entity.piggybank.AccountHolder()
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "인증 코드 확인에 실패했습니다.",
                            registerAccount = current.copy(
                                attemptsLeft = left,
                                code = "",
                                codeError = null
                            )
                        )
                    }
                }
            }
        }
    }


    fun resendOneWon() {
        val accountNo = _uiState.value.accountHolder.accountNo
        if (accountNo.isBlank()) {
            _uiState.update { it.copy(errorMessage = "계좌정보가 없습니다. 처음부터 진행해주세요.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = piggyBankUseCases.requestOneWonVerificationUseCase(accountNo)
            result.onSuccess {
                // 입력 중이던 코드 초기화 + SENDCODE 화면으로 되돌리기
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        registerAccount = it.registerAccount.copy(
                            code = "",
                            codeError = null
                        ),
                        registerAccountStep = RegisterAccountStep.SENDCODE
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "1원 재송금에 실패했습니다."
                    )
                }
            }
        }
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
                code = code,
                codeError = error
            )
        )
    }

}