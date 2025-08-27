package com.ssafy.tiggle.presentation.ui.dutchpay

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.tiggle.core.utils.Formatter
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayItem
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPaySummary
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGray
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText

@Composable
fun DutchPayStatusScreen(
    onBackClick: () -> Unit,
    viewModel: DutchPayStatusViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    TiggleScreenLayout(
        showBackButton = true,
        title = "더치페이 현황",
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

            uiState.error != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = uiState.error!!,
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadDutchPaySummary() }) {
                        Text("다시 시도")
                    }
                }
            }

            uiState.summary != null -> {
                DutchPayStatusContent(
                    summary = uiState.summary!!,
                    uiState = uiState,
                    onTabSelected = viewModel::onTabSelected,
                    onLoadMore = viewModel::loadMoreItems
                )
            }
        }
    }
}

@Composable
private fun DutchPayItemCard(
    item: DutchPayItem,
    isCompletedTab: Boolean = false
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 헤더: 아이콘과 제목
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 아이콘
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = TiggleBlue.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "💰",
                        fontSize = 20.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // 제목
                Text(
                    text = item.title,
                    style = AppTypography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 금액 정보 (회색 배경 박스)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isCompletedTab) "내가 낸 금액" else "내가 낼 금액",
                            style = AppTypography.bodySmall,
                            color = Color(0xFF666666)
                        )
                        Text(
                            text = "총 ${Formatter.formatCurrency(item.totalAmount.toLong())}",
                            style = AppTypography.bodySmall,
                            color = Color(0xFF999999)
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = Formatter.formatCurrency(item.myAmount.toLong()),
                            style = AppTypography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = TiggleBlue
                            )
                        )
                        Text(
                            text = "${item.participantCount}명 참여",
                            style = AppTypography.bodySmall,
                            color = Color(0xFF666666)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 참여 현황
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "참여 현황",
                    style = AppTypography.bodySmall,
                    color = Color(0xFF666666)
                )
                Text(
                    text = "${item.paidCount}/${item.participantCount}명 참여",
                    style = AppTypography.bodySmall,
                    color = Color(0xFF666666)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 애니메이션 진행률 바
            AnimatedProgressBar(
                progress = if (item.participantCount > 0) item.paidCount.toFloat() / item.participantCount else 0f
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 하단: 요청자와 날짜
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (item.isCreator) "나의 요청" else "다른 사람의 요청",
                    style = AppTypography.bodySmall,
                    color = Color(0xFF999999)
                )
                
                Text(
                    text = Formatter.formatDateTime(item.requestedAt),
                    style = AppTypography.bodySmall,
                    color = Color(0xFF999999)
                )
            }
        }
    }
}

@Composable
private fun DutchPayStatusContent(
    summary: DutchPaySummary,
    uiState: DutchPayStatusUiState,
    onTabSelected: (Int) -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyListState()

    // 스크롤 끝에 도달하면 더 많은 데이터 로드
 
    LaunchedEffect(listState, uiState.selectedTabIndex) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .collect { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()
                if (lastVisibleItem != null) {
                    val currentItems =
                        if (uiState.selectedTabIndex == 0) uiState.inProgressItems else uiState.completedItems
                    val hasNext =
                        if (uiState.selectedTabIndex == 0) uiState.hasNextInProgress else uiState.hasNextCompleted

                    // 마지막 아이템에 도달했고, 더 많은 데이터가 있고, 현재 로딩 중이 아닐 때만 호출
                    if (lastVisibleItem.index >= currentItems.size - 1 && hasNext && !uiState.isLoading) {
                        delay(500) // 0.5초 딜레이
                        onLoadMore()
                    }
                }
            }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
    ) {
        // 상단 요약 카드
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TiggleBlue)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${summary.totalTransferredAmount}원",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "더치페이로 모은 총 티끌",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // 통계 정보
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatisticItem(
                            value = "${summary.transferCount}회",
                            label = "티끌 적립 횟수"
                        )
                        StatisticItem(
                            value = "${summary.participatedCount}회",
                            label = "더치페이 횟수"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 탭 네비게이션
        item {
            TabRow(
                selectedTabIndex = uiState.selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = uiState.selectedTabIndex == 0,
                    onClick = { onTabSelected(0) },
                    text = { Text("진행중 (${uiState.inProgressItems.size})") }
                )
                Tab(
                    selected = uiState.selectedTabIndex == 1,
                    onClick = { onTabSelected(1) },
                    text = { Text("완료 기록 (${uiState.completedItems.size})") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 리스트 아이템들
        val currentItems =
            if (uiState.selectedTabIndex == 0) uiState.inProgressItems else uiState.completedItems

        if (currentItems.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "더치페이 기록이 없습니다",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            items(currentItems) { item ->
                DutchPayItemCard(
                    item = item,
                    isCompletedTab = uiState.selectedTabIndex == 1
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 더보기 로딩
            if (uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = TiggleBlue
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticItem(
    value: String,
    label: String
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun AnimatedProgressBar(progress: Float) {
    var isVisible by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (isVisible) progress else 0f,
        animationSpec = tween(durationMillis = 1500),
        label = "progress"
    )
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .background(
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.White,      // 흰색
                            TiggleBlue       // 파란색
                        )
                    )
                )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DutchPayStatusScreenPreview() {
    val sampleSummary = DutchPaySummary(
        totalTransferredAmount = 1200,
        transferCount = 3,
        participatedCount = 4
    )

    val sampleItems = listOf(
        DutchPayItem(
            dutchpayId = 1L,
            title = "어제 저녁 먹은거 정산",
            myAmount = 17000,
            totalAmount = 50000,
            participantCount = 3,
            paidCount = 1,
            requestedAt = "2025-08-20T12:00:00Z",
            isCreator = true
        ),
        DutchPayItem(
            dutchpayId = 2L,
            title = "택시팟",
            myAmount = 16300,
            totalAmount = 50000,
            participantCount = 4,
            paidCount = 3,
            requestedAt = "2025-08-20T10:30:00Z",
            isCreator = false
        )
    )

    val sampleUiState = DutchPayStatusUiState(
        isLoading = false,
        summary = sampleSummary,
        inProgressItems = sampleItems,
        completedItems = emptyList(),
        selectedTabIndex = 0
    )

    DutchPayStatusContent(
        summary = sampleSummary,
        uiState = sampleUiState,
        onTabSelected = {},
        onLoadMore = {}
    )
}

@Preview(showBackground = true)
@Composable
private fun DutchPayItemCardPreview() {
    val sampleItem = DutchPayItem(
        dutchpayId = 1L,
        title = "어제 저녁 먹은거 정산",
        myAmount = 17000,
        totalAmount = 50000,
        participantCount = 3,
        paidCount = 1,
        requestedAt = "2025-08-20T12:00:00Z",
        isCreator = true
    )

    DutchPayItemCard(
        item = sampleItem,
        isCompletedTab = false
    )
}
