package com.ssafy.tiggle.presentation.ui.donation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.ssafy.tiggle.domain.entity.donation.DonationRank
import com.ssafy.tiggle.presentation.ui.components.TiggleButton
import com.ssafy.tiggle.presentation.ui.components.TiggleButtonVariant
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText

@Composable
fun DonationRankingScreen(
    onNavigateBack: () -> Unit,
    viewModel: DonationRankingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    TiggleScreenLayout(
        title = "랭킹",
        showBackButton = true,
        onBackClick = onNavigateBack,
        enableScroll = false,
        contentPadding = PaddingValues(0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 서브타이틀
            Text(
                text = "티끌이 만든 태산을 확인해보세요",
                fontSize = 14.sp,
                color = TiggleGrayText,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp)
            )

            // 탭 네비게이션
            RankingTabNavigation(
                selectedTab = uiState.selectedTab,
                onTabSelected = viewModel::onTabSelected
            )

            // 랭킹 리스트
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TiggleBlue)
                    }
                }

                uiState.error != null -> {
                    ErrorContent(
                        error = uiState.error!!,
                        onRetry = viewModel::retry
                    )
                }

                else -> {
                    val rankingList = when (uiState.selectedTab) {
                        RankingTab.UNIVERSITY -> uiState.universityRanking
                        RankingTab.DEPARTMENT -> uiState.departmentRanking
                    }

                    RankingList(
                        rankingList = rankingList,
                        selectedTab = uiState.selectedTab
                    )
                }
            }
        }
    }
}

@Composable
fun RankingTabNavigation(
    selectedTab: RankingTab,
    onTabSelected: (RankingTab) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(TiggleGrayLight)
            .padding(4.dp)
    ) {
        RankingTab(
            text = "학교 기부 랭킹",
            isSelected = selectedTab == RankingTab.UNIVERSITY,
            onClick = { onTabSelected(RankingTab.UNIVERSITY) },
            modifier = Modifier.weight(1f)
        )
        RankingTab(
            text = "학과 기부 랭킹",
            isSelected = selectedTab == RankingTab.DEPARTMENT,
            onClick = { onTabSelected(RankingTab.DEPARTMENT) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun RankingTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(
                if (isSelected) Color.White else Color.Transparent
            )
            .padding(vertical = 12.dp)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) TiggleBlue else TiggleGrayText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun RankingList(
    rankingList: List<DonationRank>,
    selectedTab: RankingTab
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 32.dp),
    ) {
        items(rankingList) { rank ->
            RankingItem(
                rank = rank,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun RankingItem(
    rank: DonationRank,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(vertical = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 순위 표시
        RankingBadge(rank = rank.rank)

        Spacer(modifier = Modifier.width(16.dp))

        // 이름
        Text(
            text = rank.name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        // 금액
        Text(
            text = "${rank.amount.toString().reversed().chunked(3).joinToString(",").reversed()}원",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
        thickness = 1.dp,
        color = TiggleGrayLight
    )
}

@Composable
fun RankingBadge(rank: Int) {
    when (rank) {
        1 -> {
            // 금메달
            Image(
                painter = painterResource(id = R.drawable.gold_medal),
                contentDescription = "금메달",
                modifier = Modifier.size(40.dp)
            )
        }

        2 -> {
            // 은메달
            Image(
                painter = painterResource(id = R.drawable.silver_medal),
                contentDescription = "은메달",
                modifier = Modifier.size(40.dp)
            )
        }

        3 -> {
            // 동메달
            Image(
                painter = painterResource(id = R.drawable.bronze_medal),
                contentDescription = "동메달",
                modifier = Modifier.size(40.dp)
            )
        }

        else -> {
            // 일반 순위
            Box(
                modifier = Modifier
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD9D9D9)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = rank.toString(),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

        }
    }
}

@Composable
fun ErrorContent(
    error: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = error,
            fontSize = 16.sp,
            color = TiggleGrayText,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TiggleButton(
            text = "다시 시도",
            onClick = onRetry,
            variant = TiggleButtonVariant.Primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DonationRankingScreenPreview() {
    val previewRanking = listOf(
        DonationRank(1, "헤이영 대학교", 239285290),
        DonationRank(2, "싸피 대학교", 239285290),
        DonationRank(3, "싸피 대학교", 239285290),
        DonationRank(4, "구미 대학교", 239285290)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        RankingTabNavigation(
            selectedTab = RankingTab.UNIVERSITY,
            onTabSelected = {}
        )

        RankingList(
            rankingList = previewRanking,
            selectedTab = RankingTab.UNIVERSITY
        )
    }
}
