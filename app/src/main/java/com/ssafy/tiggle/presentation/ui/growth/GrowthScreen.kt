package com.ssafy.tiggle.presentation.ui.growth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.tiggle.R
import com.ssafy.tiggle.core.utils.Formatter
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText
import com.ssafy.tiggle.presentation.ui.theme.TiggleSkyBlue

@Composable
fun GrowthScreen(
    modifier: Modifier = Modifier,
    onDonationHistoryClick: () -> Unit = {},
    onDonationStatusClick: () -> Unit = {},
    onDonationRankingClick: () -> Unit = {},
    viewModel: GrowthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    TiggleScreenLayout(
        showBackButton = false,
        showLogo = false
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            Spacer(Modifier.height(30.dp))

            // 제목
            Text(
                text = "나의 성장",
                color = Color.Black,
                fontSize = 22.sp,
                style = AppTypography.headlineLarge
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "작은 기부가 만든 변화를 확인해보세요",
                color = TiggleGrayText,
                fontSize = 13.sp,
                style = AppTypography.bodySmall
            )
            Spacer(Modifier.height(30.dp))

                         // 성장 카드 (아이콘들 포함)
             GrowthCard(
                 uiState=uiState,
                 onDonationHistoryClick = onDonationHistoryClick,
                 onDonationStatusClick = onDonationStatusClick,
                 onDonationRankingClick = onDonationRankingClick
             )
        }
    }
}

@Composable
private fun GrowthIconRow(
    onDonationHistoryClick: () -> Unit,
    onDonationStatusClick: () -> Unit,
    onDonationRankingClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        // 기부 기록
        Spacer(Modifier.width(6.dp))
        GrowthIconItem(
            iconRes = R.drawable.donation_history_icon,
            label = "기부 기록",
            onClick = onDonationHistoryClick
        )
        Spacer(Modifier.width(6.dp))
        // 현황
        GrowthIconItem(
            iconRes = R.drawable.donation_status_icon,
            label = "현황",
            onClick = onDonationStatusClick
        )
        Spacer(Modifier.width(6.dp))
        // 랭킹
        GrowthIconItem(
            iconRes = R.drawable.donation_ranking,
            label = "랭킹",
            onClick = onDonationRankingClick
        )
    }
}

@Composable
private fun GrowthIconItem(
    iconRes: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(50.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GrowthCard(
    uiState: GrowthUiState,
    onDonationHistoryClick: () -> Unit,
    onDonationStatusClick: () -> Unit,
    onDonationRankingClick: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TiggleSkyBlue),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 기부 관련 아이콘들 (기부 기록, 현황, 랭킹)
            GrowthIconRow(
                onDonationHistoryClick = onDonationHistoryClick,
                onDonationStatusClick = onDonationStatusClick,
                onDonationRankingClick = onDonationRankingClick
            )

            Spacer(Modifier.height(24.dp))

            // 캐릭터 이미지 (현재는 비워둠)
            Box(Modifier.fillMaxWidth().height(500.dp).background(Color.Transparent) ) {
                Character3D(level = uiState.growth.level, modifier = Modifier.fillMaxSize())
            }

            Spacer(Modifier.height(20.dp))

            // 레벨 정보
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "레벨 ${uiState.growth.level+1}",
                    fontSize = 14.sp,
                    color = TiggleGrayText,
                    modifier = Modifier
                        .background(
                            Color.White.copy(alpha = 0.7f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )

                Spacer(Modifier.width(12.dp))

                Text(
                    text = "쏠",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TiggleBlue
                )
            }

            Spacer(Modifier.height(16.dp))

            // 총 티끌 금액
            Text(
                text = "총 티끌: ${Formatter.formatCurrency(uiState.growth.totalAmount)}",
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(12.dp))

            // 진행 바
            LinearProgressIndicator(
                progress = { 0.7f }, // TODO: 실제 진행률 계산
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = TiggleBlue,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Spacer(Modifier.height(8.dp))

            // 다음 레벨까지 필요한 금액
            Text(
                text = "다음 레벨까지 ${Formatter.formatCurrency(uiState.growth.toNextLevel.toLong())}",
                fontSize = 12.sp,
                color = TiggleGrayText
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GrowthScreenPreview() {
    GrowthScreen()
}
