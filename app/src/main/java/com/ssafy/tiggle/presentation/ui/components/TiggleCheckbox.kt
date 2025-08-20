package com.ssafy.tiggle.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
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
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText

/**
 * 체크박스 타입
 */
enum class CheckboxType {
    BASIC,  // 체크 마크만
    ROUND   // 둥근 배경 + 체크 마크
}

/**
 * 통합 체크박스 컴포넌트
 * 타입에 따라 다른 스타일의 체크박스를 렌더링
 */
@Composable
fun TiggleCheckbox(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    type: CheckboxType = CheckboxType.BASIC,
    modifier: Modifier = Modifier,
    size: Int = 24
) {
    when (type) {
        CheckboxType.BASIC -> {
            // 기본 체크박스 (체크 마크만)
            Box(
                modifier = modifier
                    .size(size.dp)
                    .clickable { onCheckedChange(!isChecked) },
                contentAlignment = Alignment.Center
            ) {
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "선택됨",
                        tint = TiggleBlue,
                        modifier = Modifier.size((size * 0.8).dp)
                    )
                }
            }
        }
        
        CheckboxType.ROUND -> {
            // 둥근 체크박스 (배경 + 체크 마크)
            Box(
                modifier = modifier
                    .size(size.dp)
                    .background(
                        color = if (isChecked) TiggleBlue else Color.Transparent,
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = if (isChecked) TiggleBlue else TiggleGrayText,
                        shape = CircleShape
                    )
                    .clickable { onCheckedChange(!isChecked) },
                contentAlignment = Alignment.Center
            ) {
                if (isChecked) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "선택됨",
                        tint = Color.White,
                        modifier = Modifier.size((size * 0.6).dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TiggleCheckboxPreview() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "체크박스 타입 비교",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 둥근 체크박스
        Row(verticalAlignment = Alignment.CenterVertically) {
            TiggleCheckbox(
                isChecked = true,
                onCheckedChange = {},
                type = CheckboxType.ROUND
            )
            Text(
                text = "둥근 체크박스 (선택됨)",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            TiggleCheckbox(
                isChecked = false,
                onCheckedChange = {},
                type = CheckboxType.ROUND
            )
            Text(
                text = "둥근 체크박스 (선택 안됨)",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 기본 체크박스
        Row(verticalAlignment = Alignment.CenterVertically) {
            TiggleCheckbox(
                isChecked = true,
                onCheckedChange = {},
                type = CheckboxType.BASIC
            )
            Text(
                text = "기본 체크박스 (선택됨)",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            TiggleCheckbox(
                isChecked = false,
                onCheckedChange = {},
                type = CheckboxType.BASIC
            )
            Text(
                text = "기본 체크박스 (선택 안됨)",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TermsAgreementSimplePreview() {
    var allChecked by remember { mutableStateOf(false) }
    var serviceChecked by remember { mutableStateOf(false) }
    var privacyChecked by remember { mutableStateOf(false) }
    var marketingChecked by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "약관 동의 (간단한 방법)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 전체 동의 (둥근 체크박스)
        Row(verticalAlignment = Alignment.CenterVertically) {
            TiggleCheckbox(
                isChecked = allChecked,
                onCheckedChange = { 
                    allChecked = it
                    serviceChecked = it
                    privacyChecked = it
                    marketingChecked = it
                },
                type = CheckboxType.ROUND
            )
            Text(
                text = "모두 동의합니다",
                modifier = Modifier.padding(start = 12.dp),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 개별 약관들 (기본 체크박스)
        Row(verticalAlignment = Alignment.CenterVertically) {
            TiggleCheckbox(
                isChecked = serviceChecked,
                onCheckedChange = { 
                    serviceChecked = it
                    allChecked = serviceChecked && privacyChecked && marketingChecked
                },
                type = CheckboxType.BASIC
            )
            Text(
                text = "서비스 이용약관 (필수)",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            TiggleCheckbox(
                isChecked = privacyChecked,
                onCheckedChange = { 
                    privacyChecked = it
                    allChecked = serviceChecked && privacyChecked && marketingChecked
                },
                type = CheckboxType.BASIC
            )
            Text(
                text = "개인정보 처리방침 (필수)",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            TiggleCheckbox(
                isChecked = marketingChecked,
                onCheckedChange = { 
                    marketingChecked = it
                    allChecked = serviceChecked && privacyChecked && marketingChecked
                },
                type = CheckboxType.BASIC
            )
            Text(
                text = "마케팅 정보 수신 동의 (선택)",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
