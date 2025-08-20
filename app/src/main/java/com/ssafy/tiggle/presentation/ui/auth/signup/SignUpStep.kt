package com.ssafy.tiggle.presentation.ui.auth.signup

/**
 * 회원가입 단계
 */
enum class SignUpStep {
    TERMS,          // 약관 동의
    EMAIL,          // 이메일 입력
    PASSWORD,       // 비밀번호 입력
    NAME,           // 이름 입력
    SCHOOL,         // 학교/학과/학번 입력
    COMPLETE        // 가입 완료
}