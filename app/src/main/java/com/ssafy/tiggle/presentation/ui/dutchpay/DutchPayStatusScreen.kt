package com.ssafy.tiggle.presentation.ui.dutchpay

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
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
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun DutchPayStatusScreen(
    onBackClick: () -> Unit,
    onItemClick: (Long) -> Unit = {},
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
                    Button(onClick = { /* TODO: 재시도 로직 구현 */ }) {
                        Text("다시 시도")
                    }
                }
            }

            uiState.summary != null -> {
                DutchPayStatusContent(
                    summary = uiState.summary!!,
                    selectedTabIndex = uiState.selectedTabIndex,
                    inProgressItems = uiState.inProgressItems,
                    completedItems = uiState.completedItems,
                    hasNextInProgress = uiState.hasNextInProgress,
                    hasNextCompleted = uiState.hasNextCompleted,
                    isLoadingMore = uiState.isLoadingMore,
                    onTabSelected = viewModel::onTabSelected,
                    onLoadMore = viewModel::loadMoreItems,
                    onItemClick = onItemClick
                )
            }
        }
    }
}

// DutchPayItemCard는 변경사항 없습니다.
@Composable
private fun AnimatedNumberCounter(
    targetValue: Int,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    val animatedValue by animateIntAsState(
        targetValue = if (isVisible) targetValue else 0,
        animationSpec = tween(
            durationMillis = 1000,
            easing = androidx.compose.animation.core.LinearEasing
        ),
        label = "number_animation"
    )
    
    Text(
        text = "+ ${Formatter.formatCurrency(animatedValue.toLong())}",
        style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        color = Color(0xFF1B6BFF),
        modifier = modifier
    )
}

@Composable
private fun DutchPayItemCard(
    item: DutchPayItem,
    isCompletedTab: Boolean = false,
    onCardClick: (Long) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick(item.dutchpayId) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = TiggleBlue.copy(alpha = 0.1f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💰", fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.title,
                    style = AppTypography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFF5F5F5), shape = RoundedCornerShape(8.dp))
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
                    Column(horizontalAlignment = Alignment.End) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "참여 현황", style = AppTypography.bodySmall, color = Color(0xFF666666))
                Text(
                    text = "${item.paidCount}/${item.participantCount}명 참여",
                    style = AppTypography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            AnimatedProgressBar(
                progress = if (item.participantCount > 0) item.paidCount.toFloat() / item.participantCount else 0f
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (item.isCreator) "나의 요청" else "${item.creatorName}님의 요청",
                    style = AppTypography.bodySmall,
                    color = Color(0xFF999999)
                )
                Text(
                    text = Formatter.formatDateTime(item.requestedAt),
                    style = AppTypography.bodySmall,
                    color = Color(0xFF999999)
                )
            }
            
            // 티끌 적립 정보 표시 (tiggleAmount가 0보다 클 때만)
            if (item.tiggleAmount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFFF0F8FF),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🐷",
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "티끌 적립",
                        style = AppTypography.bodyMedium,
                        color = Color(0xFF1B6BFF),
                        modifier = Modifier.weight(1f)
                    )
                    AnimatedNumberCounter(
                        targetValue = item.tiggleAmount
                    )
                }
            }
        }
    }
}


@Composable
private fun DutchPayStatusContent(
    summary: DutchPaySummary,
    selectedTabIndex: Int,
    inProgressItems: List<DutchPayItem>,
    completedItems: List<DutchPayItem>,
    hasNextInProgress: Boolean,
    hasNextCompleted: Boolean,
    isLoadingMore: Boolean,
    onTabSelected: (Int) -> Unit,
    onLoadMore: () -> Unit,
    onItemClick: (Long) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, selectedTabIndex) { // selectedTabIndex가 바뀔 때도 다시 실행되도록
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .distinctUntilChanged()
            .collect { lastIndex ->
                if (lastIndex == null) return@collect

                val currentItems = if (selectedTabIndex == 0) inProgressItems else completedItems
                val hasNext = if (selectedTabIndex == 0) hasNextInProgress else hasNextCompleted

                // 마지막 아이템 근처에 도달했고, 다음 페이지가 있고, 로딩중이 아닐 때 호출
                if (lastIndex >= currentItems.size - 2 && hasNext && !isLoadingMore) {
                    onLoadMore()
                }
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                    // ✨ 수정된 부분: .toLong()을 추가하여 타입 에러 해결
                    text = Formatter.formatCurrency(summary.totalTransferredAmount.toLong()),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatisticItem(value = "${summary.transferCount}회", label = "티끌 적립 횟수")
                    StatisticItem(value = "${summary.participatedCount}회", label = "더치페이 횟수")
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { onTabSelected(0) },
                text = { Text("진행중 (${inProgressItems.size})") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { onTabSelected(1) },
                text = { Text("완료 기록 (${completedItems.size})") }
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            val currentItems = if (selectedTabIndex == 0) inProgressItems else completedItems

            if (currentItems.isEmpty() && !isLoadingMore) {
                item {
                    Box(
                        modifier = Modifier
                            .fillParentMaxHeight(0.5f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "더치페이 기록이 없습니다", fontSize = 16.sp, color = Color.Gray)
                    }
                }
            } else {
                items(currentItems, key = { it.dutchpayId }) { item ->
                    DutchPayItemCard(
                        item = item,
                        isCompletedTab = selectedTabIndex == 1,
                        onCardClick = onItemClick
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = TiggleBlue)
                        }
                    }
                }
            }
        }
    }
}

// StatisticItem, AnimatedProgressBar는 변경사항 없습니다.
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
                        colors = listOf(Color.White, TiggleBlue)
                    )
                )
        )
    }
}

// ✨ 수정된 부분: Preview가 새 파라미터에 맞게 값을 전달하도록 변경
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
            isCreator = true,
            creatorName = "나",
            tiggleAmount = 334
        ),
        DutchPayItem(
            dutchpayId = 2L,
            title = "택시팟",
            myAmount = 16300,
            totalAmount = 50000,
            participantCount = 4,
            paidCount = 3,
            requestedAt = "2025-08-20T10:30:00Z",
            isCreator = false,
            creatorName = "홍길동",
            tiggleAmount = 0
        )
    )

    // Preview를 위한 샘플 상태 객체
    val sampleUiState = DutchPayStatusUiState(
        isLoading = false,
        summary = sampleSummary,
        inProgressItems = sampleItems,
        completedItems = emptyList(),
        selectedTabIndex = 0
    )

    // DutchPayStatusContent 호출 시, uiState 객체 대신 개별 값들을 전달
    DutchPayStatusContent(
        summary = sampleUiState.summary!!,
        selectedTabIndex = sampleUiState.selectedTabIndex,
        inProgressItems = sampleUiState.inProgressItems,
        completedItems = sampleUiState.completedItems,
        hasNextInProgress = true, // preview용 임시값
        hasNextCompleted = false, // preview용 임시값
        isLoadingMore = false, // preview용 임시값
        onTabSelected = {},
        onLoadMore = {},
        onItemClick = {}
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
        isCreator = true,
        creatorName = "나",
        tiggleAmount = 334
    )

    DutchPayItemCard(
        item = sampleItem,
        isCompletedTab = false,
        onCardClick = {}
    )
}