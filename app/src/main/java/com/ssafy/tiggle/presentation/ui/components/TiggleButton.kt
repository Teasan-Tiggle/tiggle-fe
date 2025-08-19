package com.ssafy.tiggle.presentation.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText

/**
 * 공통 버튼 컴포넌트
 */
@Composable
fun TiggleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    variant: TiggleButtonVariant = TiggleButtonVariant.Primary
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = when (variant) {
                TiggleButtonVariant.Primary -> TiggleBlue
                TiggleButtonVariant.Secondary -> TiggleGrayLight
                TiggleButtonVariant.Disabled -> TiggleGrayLight
            },
            contentColor = when (variant) {
                TiggleButtonVariant.Primary -> Color.White
                TiggleButtonVariant.Secondary -> TiggleBlue
                TiggleButtonVariant.Disabled -> TiggleGrayText
            }
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = when (variant) {
                    TiggleButtonVariant.Primary -> Color.White
                    else -> TiggleBlue
                },
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

enum class TiggleButtonVariant {
    Primary,
    Secondary,
    Disabled
}

@Preview(showBackground = true)
@Composable
private fun TiggleButtonPrimaryPreview() {
    TiggleButton(
        text = "로그인",
        onClick = {},
        variant = TiggleButtonVariant.Primary
    )
}

@Preview(showBackground = true)
@Composable
private fun TiggleButtonSecondaryPreview() {
    TiggleButton(
        text = "취소",
        onClick = {},
        variant = TiggleButtonVariant.Secondary
    )
}

@Preview(showBackground = true)
@Composable
private fun TiggleButtonDisabledPreview() {
    TiggleButton(
        text = "비활성화",
        onClick = {},
        enabled = false,
        variant = TiggleButtonVariant.Disabled
    )
}

@Preview(showBackground = true)
@Composable
private fun TiggleButtonLoadingPreview() {
    TiggleButton(
        text = "로딩중...",
        onClick = {},
        isLoading = true,
        variant = TiggleButtonVariant.Primary
    )
}
