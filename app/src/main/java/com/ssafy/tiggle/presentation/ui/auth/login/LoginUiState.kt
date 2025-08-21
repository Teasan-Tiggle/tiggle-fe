package com.ssafy.tiggle.presentation.ui.auth.login

/**
 * 로그인 화면의 UI 상태를 나타내는 데이터 클래스
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoginSuccess: Boolean = false,
    
    // 필드별 에러 메시지
    val emailError: String? = null,
    val passwordError: String? = null,
    
    // 전체 에러 메시지 (로그인 실패 등)
    val generalError: String? = null
)
