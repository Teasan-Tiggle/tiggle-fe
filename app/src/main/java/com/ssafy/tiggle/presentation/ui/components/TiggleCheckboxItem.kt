package com.ssafy.tiggle.presentation.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue

/**
 * 둥근 체크박스를 사용하는 "모두 동의" 아이템 (테두리와 화살표 포함)
 */
@Composable
fun TiggleAllAgreeCheckboxItem(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onDetailClick: (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = TiggleBlue,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TiggleCheckbox(
            type = CheckboxType.ROUND,
            isChecked = isChecked,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
                .clickable { onCheckedChange(!isChecked) } // 텍스트 클릭 시 토글
        )

        // 화살표 아이콘 (상세 페이지로 이동)
        if (onDetailClick != null) {
            IconButton(
                onClick = onDetailClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "상세보기",
                    tint = Color.Gray
                )
            }
        }
    }
}

/**
 * 일반 체크박스 리스트 아이템 (기본 체크박스 사용)
 */
@Composable
fun TiggleCheckboxItem(
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onDetailClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TiggleCheckbox(
            type = CheckboxType.BASIC,
            isChecked = isChecked,
            onCheckedChange = onCheckedChange
        )

        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .clickable { onCheckedChange(!isChecked) } // 텍스트 클릭 시 토글
        )

        if (onDetailClick != null) {
            IconButton(
                onClick = onDetailClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "상세보기",
                    tint = Color.Gray
                )
            }
        }


    }
}

@Preview(showBackground = true)
@Composable
private fun TiggleAllAgreeCheckboxItemPreview() {
    TiggleAllAgreeCheckboxItem(
        text = "모두 동의합니다.",
        isChecked = true,
        onCheckedChange = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun TiggleCheckboxItemCheckedPreview() {
    TiggleCheckboxItem(
        text = "이용약관(필수)",
        isChecked = true,
        onCheckedChange = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun TiggleCheckboxItemUncheckedPreview() {
    TiggleCheckboxItem(
        text = "마케팅 정보 수신 동의(선택)",
        isChecked = false,
        onCheckedChange = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun TiggleTermsAgreementPreview() {
    var allTerms by remember { mutableStateOf(false) }
    var serviceTerms by remember { mutableStateOf(false) }
    var privacyPolicy by remember { mutableStateOf(false) }
    var marketing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // 모두 동의 (둥근 체크박스 + 테두리)
        TiggleAllAgreeCheckboxItem(
            text = "모두 동의합니다.",
            isChecked = allTerms,
            onCheckedChange = {
                allTerms = it
                serviceTerms = it
                privacyPolicy = it
                marketing = it
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 개별 약관들 (기본 체크박스)
        TiggleCheckboxItem(
            text = "이용약관(필수)",
            isChecked = serviceTerms,
            onCheckedChange = {
                serviceTerms = it
                allTerms = serviceTerms && privacyPolicy && marketing
            },
            onDetailClick = { /* 약관 상세 보기 */ }
        )

        TiggleCheckboxItem(
            text = "개인정보 수집 및 이용동의(필수)",
            isChecked = privacyPolicy,
            onCheckedChange = {
                privacyPolicy = it
                allTerms = serviceTerms && privacyPolicy && marketing
            },
            onDetailClick = { /* 약관 상세 보기 */ }
        )

        TiggleCheckboxItem(
            text = "마케팅 정보 수신 동의(선택)",
            isChecked = marketing,
            onCheckedChange = {
                marketing = it
                allTerms = serviceTerms && privacyPolicy && marketing
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TiggleCheckboxItemInteractivePreview() {
    var isChecked by remember { mutableStateOf(false) }

    Column {
        Text(
            text = "클릭해서 상태를 변경해보세요",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TiggleCheckboxItem(
            text = "인터랙티브 체크박스",
            isChecked = isChecked,
            onCheckedChange = { isChecked = it }
        )

        Text(
            text = "현재 상태: ${if (isChecked) "선택됨" else "선택 안됨"}",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}