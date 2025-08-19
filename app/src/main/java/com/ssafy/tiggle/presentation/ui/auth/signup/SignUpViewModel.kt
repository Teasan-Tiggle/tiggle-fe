package com.ssafy.tiggle.presentation.ui.auth.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.entity.ValidationField
import com.ssafy.tiggle.domain.usecase.SignUpUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 회원가입 전체 과정을 관리하는 ViewModel
 */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUserUseCase: SignUpUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    // 약관 동의 관련
    fun updateTermsAgreement(type: TermsType, isChecked: Boolean) {
        val currentTerms = _uiState.value.termsData
        val newTerms = when (type) {
            TermsType.SERVICE -> currentTerms.copy(serviceTerms = isChecked)
            TermsType.PRIVACY -> currentTerms.copy(privacyPolicy = isChecked)
            TermsType.MARKETING -> currentTerms.copy(marketingOptional = isChecked)
            TermsType.LOCATION -> currentTerms.copy(locationOptional = isChecked)
            TermsType.ALL -> currentTerms.copy(
                serviceTerms = isChecked,
                privacyPolicy = isChecked,
                marketingOptional = isChecked,
                locationOptional = isChecked
            )
        }

        _uiState.value = _uiState.value.copy(termsData = newTerms)
    }

    // 사용자 데이터 업데이트 (도메인 엔티티의 유효성 검사 사용)
    fun updateEmail(email: String) {
        val currentData = _uiState.value.userData
        val newData = currentData.copy(email = email).validateField(ValidationField.EMAIL)
        _uiState.value = _uiState.value.copy(userData = newData)
    }

    fun updatePassword(password: String) {
        val currentData = _uiState.value.userData
        val newData = currentData.copy(password = password)
            .validateField(ValidationField.PASSWORD)
            .validateField(ValidationField.CONFIRM_PASSWORD) // 비밀번호 확인도 재검사

        _uiState.value = _uiState.value.copy(userData = newData)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        val currentData = _uiState.value.userData
        val newData = currentData.copy(confirmPassword = confirmPassword)
            .validateField(ValidationField.CONFIRM_PASSWORD)
        _uiState.value = _uiState.value.copy(userData = newData)
    }

    fun updateName(name: String) {
        val currentData = _uiState.value.userData
        val newData = currentData.copy(name = name).validateField(ValidationField.NAME)
        _uiState.value = _uiState.value.copy(userData = newData)
    }

    fun updateSchool(school: String) {
        val currentData = _uiState.value.userData
        val newData = currentData.copy(school = school).validateField(ValidationField.SCHOOL)
        _uiState.value = _uiState.value.copy(userData = newData)
    }

    fun updateDepartment(department: String) {
        val currentData = _uiState.value.userData
        val newData = currentData.copy(department = department).validateField(ValidationField.DEPARTMENT)
        _uiState.value = _uiState.value.copy(userData = newData)
    }

    fun updateStudentId(studentId: String) {
        val currentData = _uiState.value.userData
        val newData = currentData.copy(studentId = studentId).validateField(ValidationField.STUDENT_ID)
        _uiState.value = _uiState.value.copy(userData = newData)
    }

    // 단계 이동
    fun goToNextStep(): Boolean {
        val currentStep = _uiState.value.currentStep
        val canProceed = validateCurrentStep()

        if (canProceed) {
            val nextStep = when (currentStep) {
                SignUpStep.TERMS -> SignUpStep.EMAIL
                SignUpStep.EMAIL -> SignUpStep.PASSWORD
                SignUpStep.PASSWORD -> SignUpStep.NAME
                SignUpStep.NAME -> SignUpStep.SCHOOL
                SignUpStep.SCHOOL -> SignUpStep.COMPLETE
                SignUpStep.COMPLETE -> SignUpStep.COMPLETE
            }

            _uiState.value = _uiState.value.copy(currentStep = nextStep)
        }

        return canProceed
    }

    fun goToPreviousStep() {
        val currentStep = _uiState.value.currentStep
        val previousStep = when (currentStep) {
            SignUpStep.TERMS -> SignUpStep.TERMS
            SignUpStep.EMAIL -> SignUpStep.TERMS
            SignUpStep.PASSWORD -> SignUpStep.EMAIL
            SignUpStep.NAME -> SignUpStep.PASSWORD
            SignUpStep.SCHOOL -> SignUpStep.NAME
            SignUpStep.COMPLETE -> SignUpStep.SCHOOL
        }

        _uiState.value = _uiState.value.copy(currentStep = previousStep)
    }

    private fun validateCurrentStep(): Boolean {
        val currentState = _uiState.value

        return when (currentState.currentStep) {
            SignUpStep.TERMS -> {
                if (!currentState.termsData.allRequired) {
                    _uiState.value = currentState.copy(
                        errorMessage = "필수 약관에 동의해주세요."
                    )
                    false
                } else {
                    true
                }
            }

            SignUpStep.EMAIL -> {
                val validatedData = currentState.userData.validateField(ValidationField.EMAIL)

                if (validatedData.emailError != null) {
                    _uiState.value = currentState.copy(userData = validatedData)
                    false
                } else {
                    true
                }
            }

            SignUpStep.PASSWORD -> {
                val validatedData = currentState.userData
                    .validateField(ValidationField.PASSWORD)
                    .validateField(ValidationField.CONFIRM_PASSWORD)

                if (validatedData.passwordError != null || validatedData.confirmPasswordError != null) {
                    _uiState.value = currentState.copy(userData = validatedData)
                    false
                } else {
                    true
                }
            }

            SignUpStep.NAME -> {
                val validatedData = currentState.userData.validateField(ValidationField.NAME)

                if (validatedData.nameError != null) {
                    _uiState.value = currentState.copy(userData = validatedData)
                    false
                } else {
                    true
                }
            }

            SignUpStep.SCHOOL -> {
                val validatedData = currentState.userData
                    .validateField(ValidationField.SCHOOL)
                    .validateField(ValidationField.DEPARTMENT)
                    .validateField(ValidationField.STUDENT_ID)

                if (validatedData.schoolError != null || 
                    validatedData.departmentError != null || 
                    validatedData.studentIdError != null) {
                    _uiState.value = currentState.copy(userData = validatedData)
                    false
                } else {
                    true
                }
            }

            SignUpStep.COMPLETE -> true
        }
    }

    // 유효성 검사는 도메인 엔티티(UserSignUp)에서 처리합니다.

    // 회원가입 완료
    fun completeSignUp() {
        val currentState = _uiState.value

        // 최종 유효성 검사
        val validatedData = currentState.userData.withValidation()
        if (!validatedData.isValid()) {
            _uiState.value = currentState.copy(
                userData = validatedData,
                errorMessage = "입력 정보를 다시 확인해주세요."
            )
            return
        }

        // 로딩 시작
        _uiState.value = currentState.copy(
            isLoading = true,
            errorMessage = null
        )

        // 회원가입 API 호출
        viewModelScope.launch {
            signUpUserUseCase(validatedData)
                .onSuccess { user ->
                    // 회원가입 성공
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        currentStep = SignUpStep.COMPLETE,
                        errorMessage = null
                    )
                }
                .onFailure { exception ->
                    // 회원가입 실패
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "회원가입에 실패했습니다."
                    )
                }
        }
    }
}


