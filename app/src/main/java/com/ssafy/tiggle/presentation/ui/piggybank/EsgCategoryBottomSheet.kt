package com.ssafy.tiggle.presentation.ui.piggybank

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.tiggle.R
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EsgCategoryBottomSheet(
    show: Boolean,
    selectedId: Int?,
    onPick: (Int) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!show) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = Color.White
    ) {
        Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Text("자동 기부 분야 설정", style = AppTypography.headlineLarge, fontSize = 18.sp)
            Spacer(Modifier.height(6.dp))
            Text(
                "자동 기부 설정 시 기부할 분야를 선택해주세요.",
                style = AppTypography.bodySmall,
                color = TiggleGrayText
            )

            Spacer(Modifier.height(16.dp))

            // 간단한 3옵션 선택 (Planet / People / Prosperity)
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EsgChoiceChip(
                        "Planet",
                        id = 1,
                        selected = selectedId == 1,
                        imageUrl = R.drawable.icon_planet,
                        onClick = { onPick(1) })
                    EsgChoiceChip(
                        "People",
                        id = 2,
                        selected = selectedId == 2,
                        imageUrl = R.drawable.icon_people,
                        onClick = { onPick(2) })
                    EsgChoiceChip(
                        "Prosperity",
                        id = 3,
                        selected = selectedId == 3,
                        imageUrl = R.drawable.icon_prosperity,
                        onClick = { onPick(3) })
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = onConfirm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TiggleBlue,
                    contentColor = Color.White
                ),
                enabled = selectedId != null
            ) { Text("확인", style = AppTypography.bodyLarge) }

            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun EsgChoiceChip(
    label: String,
    id: Int,
    selected: Boolean,
    imageUrl: Int,
    onClick: () -> Unit
) {
    val border = if (selected) TiggleBlue else TiggleGrayText
    val CHIP_WIDTH = 110.dp
    val CHIP_HEIGHT = 100.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .size(width = CHIP_WIDTH, height = CHIP_HEIGHT)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = painterResource(id = imageUrl),
            contentDescription = "아이콘",
            modifier = Modifier.size(25.dp)
        )
        Text(
            label,
            color = TiggleGrayText,
            style = AppTypography.bodyMedium,
            fontSize = 15.sp,
            textAlign = TextAlign.Center
        )
    }
}

