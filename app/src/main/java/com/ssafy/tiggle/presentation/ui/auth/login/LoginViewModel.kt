package com.ssafy.tiggle.presentation.ui.auth.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 로그인 화면의 ViewModel
 */
class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * 이메일 입력값 업데이트
     */
    fun updateEmail(email: String) {
        val emailError = validateEmail(email)
        _uiState.value = _uiState.value.copy(
            email = email,
            emailError = emailError,
            generalError = null
        )
    }

    /**
     * 비밀번호 입력값 업데이트
     */
    fun updatePassword(password: String) {
        val passwordError = validatePassword(password)
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = passwordError,
            generalError = null
        )
    }

    /**
     * 로그인 수행
     */
    fun login() {
        val currentState = _uiState.value

        // 최종 유효성 검사
        val emailError = validateEmail(currentState.email)
        val passwordError = validatePassword(currentState.password)

        if (emailError != null || passwordError != null) {
            _uiState.value = currentState.copy(
                emailError = emailError,
                passwordError = passwordError
            )
            return
        }

        // 로딩 상태로 변경
        _uiState.value = currentState.copy(
            isLoading = true,
            emailError = null,
            passwordError = null,
            generalError = null
        )

        // TODO: 실제 로그인 로직 구현
        // 임시로 로그인 성공 처리 (실제로는 API 응답에 따라 처리)
        _uiState.value = currentState.copy(
            isLoading = false,
            isLoginSuccess = true
        )
    }

    /**
     * 로그인 성공 상태 리셋 (네비게이션 완료 후 호출)
     */
    fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(isLoginSuccess = false)
    }

    /**
     * 이메일 유효성 검사
     */
    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> null // 빈 값일 때는 에러 표시 안함 (입력 중일 수 있음)
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "올바른 이메일 형식을 입력해주세요."

            else -> null
        }
    }

    /**
     * 비밀번호 유효성 검사
     */
    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> null // 빈 값일 때는 에러 표시 안함
            password.length < 6 -> "비밀번호는 6자 이상이어야 합니다."
            else -> null
        }
    }
}
