package com.ssafy.tiggle.presentation.ui.auth.login

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ssafy.tiggle.presentation.ui.components.TiggleButton
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.components.TiggleTextField
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText

/**
 * 로그인 화면 컴포저블
 */
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    TiggleScreenLayout(
        showBackButton = false,
        showLogo = true,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 입력 필드들
            TiggleTextField(
                value = uiState.email,
                onValueChange = viewModel::updateEmail,
                label = "이메일",
                placeholder = "이메일을 입력해주세요",
                keyboardType = KeyboardType.Email,
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TiggleTextField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                label = "비밀번호",
                placeholder = "비밀번호를 입력해주세요",
                isPassword = true,
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError,
                modifier = Modifier.fillMaxWidth()
            )

            // 비밀번호 찾기
            Text(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.End)
                    .clickable { /* TODO: 비밀번호 찾기 기능 */ },
                text = "비밀번호를 잊으셨나요?",
                color = TiggleGrayText,
                fontSize = 14.sp
            )

            // 전체 에러 메시지 (로그인 실패 등)
            uiState.generalError?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            TiggleButton(
                text = "로그인",
                onClick = viewModel::login,
                isLoading = uiState.isLoading,
                enabled = uiState.email.isNotBlank() && uiState.password.isNotBlank()
            )

            Spacer(modifier = Modifier.height(24.dp))

            SignUpText(onClick = onSignUpClick)
        }
    }
}

@Composable
private fun SignUpText(
    onClick: () -> Unit
) {
    val annotatedText = buildAnnotatedString {
        withStyle(style = SpanStyle(color = TiggleGrayText)) {
            append("계정이 없으신가요? ")
        }
        withStyle(style = SpanStyle(color = TiggleBlue)) {
            append("회원가입")
        }
    }

    Text(
        text = annotatedText,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        // 위치를 오른쪽
        modifier = Modifier.clickable { onClick() }
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    LoginScreen(
        onLoginSuccess = {},
        onSignUpClick = {}
    )
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun LoginScreenWithDataPreview() {
    val viewModel = LoginViewModel().apply {
        updateEmail("user@example.com")
        updatePassword("password123")
    }

    LoginScreen(
        viewModel = viewModel,
        onLoginSuccess = {},
        onSignUpClick = {}
    )
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun LoginScreenErrorPreview() {
    val viewModel = LoginViewModel().apply {
        updateEmail("invalid-email")
        updatePassword("123") // 너무 짧은 비밀번호
    }

    LoginScreen(
        viewModel = viewModel,
        onLoginSuccess = {},
        onSignUpClick = {}
    )
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
private fun LoginScreenLoadingPreview() {
    // 로딩 상태는 실제 앱에서 확인하고, Preview에서는 기본 상태로 표시
    val viewModel = LoginViewModel().apply {
        updateEmail("user@example.com")
        updatePassword("password123")
    }

    LoginScreen(
        viewModel = viewModel,
        onLoginSuccess = {},
        onSignUpClick = {}
    )
}
