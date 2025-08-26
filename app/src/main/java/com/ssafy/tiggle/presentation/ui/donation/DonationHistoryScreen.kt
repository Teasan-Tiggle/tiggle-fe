package com.ssafy.tiggle.presentation.ui.donation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.ssafy.tiggle.domain.entity.donation.DonationCategory
import com.ssafy.tiggle.domain.entity.donation.DonationHistory
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun DonationHistoryScreen(
    onBackClick: () -> Unit = {},
    viewModel: DonationHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadDonationHistory()
    }

    TiggleScreenLayout(
        title = "나의 기부 기록",
        showBackButton = true,
        onBackClick = onBackClick,
        enableScroll = false
    ) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TiggleBlue)
                }
            }

            !uiState.errorMessage.isNullOrEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage ?: "알 수 없는 오류가 발생했습니다.",
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                DonationHistoryContent(
                    donationHistoryList = uiState.donationHistoryList
                )
            }
        }
    }
}

@Composable
private fun DonationHistoryContent(
    donationHistoryList: List<DonationHistory>
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (donationHistoryList.isEmpty()) {
            // 기부 기록이 없을 때
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "아직 기부 기록이 없어요",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = TiggleGrayText,
                        style = AppTypography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "첫 번째 기부를 시작해보세요!",
                        fontSize = 14.sp,
                        color = TiggleGrayText,
                        style = AppTypography.bodyMedium
                    )
                }
            }
        } else {
            // 기부 기록이 있을 때
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(donationHistoryList) { donation ->
                    DonationHistoryItem(donation = donation)
                }
            }
        }
        
        // Footer 카드는 항상 맨 아래에 표시
        DonationFooterCard()
    }
}

@Composable
private fun DonationHistoryItem(
    donation: DonationHistory
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 카테고리 아이콘
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(TiggleGrayLight),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = getCategoryIconRes(donation.category)),
                contentDescription = donation.category.value,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 기부 정보
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = donation.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                style = AppTypography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatDonationDate(donation.donatedAt),
                fontSize = 12.sp,
                color = TiggleGrayText,
                style = AppTypography.bodySmall
            )
        }

        // 기부 금액
        Text(
            text = "${Formatter.formatCurrency(donation.amount.toLong())}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            style = AppTypography.bodyLarge
        )
    }
}

@Composable
private fun DonationFooterCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "💡 ",
                    fontSize = 20.sp
                )

                Text(
                    text = "기부 방식",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TiggleBlue
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "티끌은 모든 과정에서 투명성과 신뢰를 " +
                        "최우선 가치로 삼습니다.\n" +
                        "기부금의 사용을 투명하게 공개하고 정해진 절차에 따라 집행하여, " +
                        "누구나 안심하고 참여할 수 있는 책임 있는 " +
                        "기부 시스템을 운영합니다.",
                fontSize = 12.sp,
                color = TiggleGrayText,
                lineHeight = 16.sp
            )
        }
    }
}

private fun getCategoryIconRes(category: DonationCategory): Int {
    return when (category) {
        DonationCategory.PLANET -> R.drawable.planet
        DonationCategory.PEOPLE -> R.drawable.people
        DonationCategory.PROSPERITY -> R.drawable.prosperity
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDonationDate(dateTimeString: String): String {
    return try {
        val dateTime = LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        val formatter = DateTimeFormatter.ofPattern("M월 d일")
        dateTime.format(formatter)
    } catch (e: Exception) {
        dateTimeString
    }
}

@Preview(showBackground = true)
@Composable
private fun DonationHistoryScreenPreview() {
    // 샘플 데이터 생성
    val sampleHistoryList = listOf(
        DonationHistory(
            category = DonationCategory.PLANET,
            donatedAt = "2024-08-18T19:38:00",
            amount = 3450,
            title = "Planet"
        ),
        DonationHistory(
            category = DonationCategory.PEOPLE,
            donatedAt = "2024-07-18T14:20:00",
            amount = 780,
            title = "People"
        ),
        DonationHistory(
            category = DonationCategory.PEOPLE,
            donatedAt = "2024-07-18T11:15:00",
            amount = 780,
            title = "People"
        ),
        DonationHistory(
            category = DonationCategory.PLANET,
            donatedAt = "2024-07-18T09:30:00",
            amount = 780,
            title = "Planet"
        )
    )

    DonationHistoryContent(donationHistoryList = sampleHistoryList)
}

@Preview(showBackground = true)
@Composable
private fun DonationHistoryEmptyPreview() {
    // 빈 리스트로 미리보기
    DonationHistoryContent(donationHistoryList = emptyList())
}
