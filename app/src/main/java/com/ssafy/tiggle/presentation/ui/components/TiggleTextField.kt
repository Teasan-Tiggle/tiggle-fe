package com.ssafy.tiggle.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText

/**
 * 공통 입력 필드 컴포넌트
 */
@Composable
fun TiggleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    maxLines: Int = 1,
    minLines: Int = 1
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    // 포커스가 변경될 때 키보드 관련 처리
                    if (focusState.isFocused) {
                        // 포커스가 되었을 때의 처리
                    }
                },
            placeholder = {
                Text(
                    text = placeholder,
                    color = TiggleGrayText
                )
            },
            visualTransformation = if (isPassword && !isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (isPasswordVisible) {
                                "비밀번호 숨기기"
                            } else {
                                "비밀번호 보기"
                            },
                            tint = TiggleGrayText
                        )
                    }
                }
            } else null,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = if (maxLines == 1) ImeAction.Next else ImeAction.Done
            ),
            singleLine = maxLines == 1,
            maxLines = maxLines,
            minLines = minLines,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = if (isError) MaterialTheme.colorScheme.error else TiggleGrayLight,
                focusedBorderColor = if (isError) MaterialTheme.colorScheme.error else TiggleBlue,
                unfocusedContainerColor = TiggleGrayLight,
                focusedContainerColor = Color.White
            ),
            isError = isError
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TiggleTextFieldPreview() {
    var text by remember { mutableStateOf("") }
    
    TiggleTextField(
        value = text,
        onValueChange = { text = it },
        label = "이메일",
        placeholder = "이메일을 입력해주세요",
        keyboardType = KeyboardType.Email
    )
}

@Preview(showBackground = true)
@Composable
private fun TiggleTextFieldPasswordPreview() {
    var password by remember { mutableStateOf("") }
    
    TiggleTextField(
        value = password,
        onValueChange = { password = it },
        label = "비밀번호",
        placeholder = "비밀번호를 입력해주세요",
        isPassword = true
    )
}

@Preview(showBackground = true)
@Composable
private fun TiggleTextFieldErrorPreview() {
    var text by remember { mutableStateOf("invalid@") }
    
    TiggleTextField(
        value = text,
        onValueChange = { text = it },
        label = "이메일",
        placeholder = "이메일을 입력해주세요",
        keyboardType = KeyboardType.Email,
        isError = true,
        errorMessage = "올바른 이메일 형식을 입력해주세요."
    )
}
