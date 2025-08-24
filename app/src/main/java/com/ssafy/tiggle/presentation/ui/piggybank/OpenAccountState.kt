package com.ssafy.tiggle.presentation.ui.piggybank

import com.ssafy.tiggle.domain.entity.piggybank.OpenAccount

data class OpenAccountState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val openAccountStep: OpenAccountStep = OpenAccountStep.INFO,
    val piggyBankAccount: OpenAccount = OpenAccount(),

    // 약관 동의
    val termsData: TermsData = TermsData(),
)

/**
 * 약관 동의 데이터
 */
data class TermsData(
    val serviceTerms: Boolean = false,          // (필수) 티끌 저금통 서비스 이용약관
    val privacyPolicy: Boolean = false,         // (필수) 개인정보 수집·이용 동의
    val financeTerms: Boolean = false,          // (필수) 전자금융거래 기본약관
    val marketingOptional: Boolean = false      // (선택) 마케팅 정보 수신 동의
) {
    // 필수 약관 전체 동의 여부
    val allRequired: Boolean
        get() = serviceTerms && privacyPolicy && financeTerms

    // 모든 약관(필수 + 선택) 전체 동의 여부
    val allTerms: Boolean
        get() = serviceTerms && privacyPolicy && financeTerms && marketingOptional
}

