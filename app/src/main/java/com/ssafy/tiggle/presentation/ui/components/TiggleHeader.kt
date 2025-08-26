package com.ssafy.tiggle.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 공통 헤더 컴포넌트
 */
@Composable
fun TiggleHeader(
    title: String? = null,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showBackButton) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = Color.Black
                    )
                }
            }

            title?.let {
                Text(
                    text = it,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(start = if (showBackButton) 8.dp else 16.dp)
                )
            }
        }
        Row{actions?.invoke(this)}
    }
}

@Preview(showBackground = true)
@Composable
private fun TiggleHeaderPreview() {
    TiggleHeader(
        title = "회원가입",
        showBackButton = true,
        onBackClick = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun TiggleHeaderWithoutBackButtonPreview() {
    TiggleHeader(
        title = "설정",
        showBackButton = false
    )
}

@Preview(showBackground = true)
@Composable
private fun TiggleHeaderOnlyBackButtonPreview() {
    TiggleHeader(
        title = null,
        showBackButton = true,
        onBackClick = {}
    )
}
