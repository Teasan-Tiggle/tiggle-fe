package com.ssafy.tiggle.presentation.ui.auth.signup

import com.ssafy.tiggle.domain.entity.UserSignUp

/**
 * 회원가입 전체 과정의 UI 상태
 */
data class SignUpUiState(
    val currentStep: SignUpStep = SignUpStep.TERMS,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // 약관 동의
    val termsData: TermsData = TermsData(),

    // 사용자 정보
    val userData: UserSignUp = UserSignUp()
)


/**
 * 약관 동의 데이터
 */
data class TermsData(
    val serviceTerms: Boolean = false,
    val privacyPolicy: Boolean = false,
    val marketingOptional: Boolean = false,
    val locationOptional: Boolean = false
) {
    val allRequired: Boolean
        get() = serviceTerms && privacyPolicy

    val allTerms: Boolean
        get() = serviceTerms && privacyPolicy && marketingOptional && locationOptional
}

