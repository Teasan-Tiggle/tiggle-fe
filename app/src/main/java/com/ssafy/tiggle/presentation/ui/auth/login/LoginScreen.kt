package com.ssafy.tiggle.presentation.ui.auth.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // 로그인 성공 시 네비게이션 처리
    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onLoginSuccess()
            viewModel.resetLoginSuccess()
        }
    }

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
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.emailError != null,
                errorMessage = uiState.emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            TiggleTextField(
                value = uiState.password,
                onValueChange = viewModel::updatePassword,
                label = "비밀번호",
                placeholder = "비밀번호를 입력해주세요",
                isPassword = true,
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.passwordError != null,
                errorMessage = uiState.passwordError,
                imeAction = ImeAction.Done
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

@Preview(showBackground = true)
@Composable
private fun LoginScreenWithDataPreview() {
    LoginScreen(
        onLoginSuccess = {},
        onSignUpClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenErrorPreview() {
    LoginScreen(
        onLoginSuccess = {},
        onSignUpClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenLoadingPreview() {
    LoginScreen(
        onLoginSuccess = {},
        onSignUpClick = {}
    )
}
