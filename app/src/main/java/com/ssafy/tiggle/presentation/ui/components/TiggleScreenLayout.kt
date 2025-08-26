package com.ssafy.tiggle.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 공통 화면 레이아웃 컴포넌트
 * 헤더 + 콘텐츠 + 하단 버튼 구조
 */
@Composable
fun TiggleScreenLayout(
    title: String? = null,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
    showLogo: Boolean = false,
    topActions: (@Composable RowScope.() -> Unit)? = null,
    bottomButton: @Composable (() -> Unit)? = null,
    contentPadding: PaddingValues = PaddingValues(horizontal = 32.dp),
    enableScroll: Boolean = true,
    content: @Composable () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .imePadding() // imePadding이 키보드 높이만큼 패딩을 자동으로 추가해줍니다.
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 헤더
            if (showBackButton || title != null) {
                TiggleHeader(
                    title = title,
                    showBackButton = showBackButton,
                    onBackClick = onBackClick,
                    actions = topActions
                )
            }

        // 메인 콘텐츠
        if (enableScroll) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 32.dp)
                    .verticalScroll(scrollState)
            ) {
                if (showLogo) {
                    TiggleLogo()
                }
                content()
            }
        } else {
            // LazyColumn 등을 사용하는 화면을 위한 레이아웃
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(contentPadding)
            ) {
                if (showLogo) {
                    Column {
                        TiggleLogo()
                        content()
                    }
                } else {
                    content()
                }
            }
        }

        // 하단 버튼 (선택적)
        bottomButton?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 32.dp)
            ) {
                it()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TiggleScreenLayoutPreview() {
    TiggleScreenLayout(
        title = "화면 제목",
        showBackButton = true,
        onBackClick = {},
        bottomButton = {
            TiggleButton(
                text = "확인",
                onClick = {}
            )
        }
    ) {
        Column {
            Text(
                text = "메인 콘텐츠 영역",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            23
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "여기에 다양한 콘텐츠가 들어갑니다.",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TiggleScreenLayoutWithLogoPreview() {
    TiggleScreenLayout(
        title = "회원가입",
        showBackButton = true,
        showLogo = true,
        onBackClick = {},
        bottomButton = {
            TiggleButton(
                text = "동의하고 가입하기",
                onClick = {}
            )
        }
    ) {
        Text(
            text = "로고가 포함된 레이아웃",
            fontSize = 16.sp
        )
    }
}
