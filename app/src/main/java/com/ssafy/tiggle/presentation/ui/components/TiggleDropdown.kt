package com.ssafy.tiggle.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText

/**
 * 공통 드롭다운 컴포넌트
 */
@Composable
fun TiggleDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    placeholder: String = "선택해주세요",
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box {
            OutlinedTextField(
                value = selectedValue.ifEmpty { placeholder },
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                enabled = false,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = TiggleGrayLight,
                    disabledContainerColor = TiggleGrayLight,
                    disabledTextColor = if (selectedValue.isEmpty()) TiggleGrayText else Color.Black
                )
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TiggleDropdownEmptyPreview() {
    TiggleDropdown(
        label = "학교",
        selectedValue = "",
        options = listOf("서울대학교", "연세대학교", "고려대학교", "SSAFY"),
        onValueChange = {},
        placeholder = "학교를 선택해주세요"
    )
}

@Preview(showBackground = true)
@Composable
private fun TiggleDropdownSelectedPreview() {
    TiggleDropdown(
        label = "학교",
        selectedValue = "SSAFY",
        options = listOf("서울대학교", "연세대학교", "고려대학교", "SSAFY"),
        onValueChange = {},
        placeholder = "학교를 선택해주세요"
    )
}

@Preview(showBackground = true)
@Composable
private fun TiggleDropdownMultiplePreview() {
    var school by remember { mutableStateOf("") }
    var major by remember { mutableStateOf("") }
    
    Column {
        Text(
            text = "학적 정보",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        TiggleDropdown(
            label = "학교",
            selectedValue = school,
            options = listOf("서울대학교", "연세대학교", "고려대학교", "SSAFY", "기타"),
            onValueChange = { school = it },
            placeholder = "학교를 선택해주세요"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TiggleDropdown(
            label = "전공",
            selectedValue = major,
            options = listOf("컴퓨터공학", "소프트웨어공학", "정보보안", "데이터사이언스", "AI"),
            onValueChange = { major = it },
            placeholder = "전공을 선택해주세요"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "선택된 값:\n학교: ${school.ifEmpty { "없음" }}\n전공: ${major.ifEmpty { "없음" }}",
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TiggleDropdownInteractivePreview() {
    var selectedOption by remember { mutableStateOf("") }
    val options = listOf("옵션 1", "옵션 2", "옵션 3", "옵션 4", "옵션 5")
    
    Column {
        Text(
            text = "드롭다운을 클릭해서 옵션을 선택해보세요",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        TiggleDropdown(
            label = "옵션 선택",
            selectedValue = selectedOption,
            options = options,
            onValueChange = { selectedOption = it }
        )
        
        if (selectedOption.isNotEmpty()) {
            Text(
                text = "선택된 옵션: $selectedOption",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
