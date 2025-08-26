package com.ssafy.tiggle.presentation.ui.donation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.ssafy.tiggle.domain.entity.donation.DonationStatus
import com.ssafy.tiggle.domain.entity.donation.DonationStatusType
import com.ssafy.tiggle.domain.entity.donation.DonationSummary
import com.ssafy.tiggle.presentation.ui.components.TiggleButton
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText

@Composable
fun DonationStatusScreen(
    onBackClick: () -> Unit = {},
    viewModel: DonationStatusViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDonationModal by remember { mutableStateOf(false) }

    TiggleScreenLayout(
        title = "기부",
        showBackButton = true,
        onBackClick = onBackClick
    ) {
        when {
            uiState.isLoading && uiState.donationSummary == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TiggleBlue)
                }
            }

            else -> {
                DonationStatusContent(
                    donationSummary = uiState.donationSummary,
                    donationStatus = uiState.donationStatus,
                    currentStatusType = uiState.currentStatusType,
                    onStatusTypeChanged = viewModel::onStatusTypeChanged,
                    onDonateClick = { showDonationModal = true },
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage
                )
            }
        }
    }

    // 기부하기 모달
    if (showDonationModal) {
        DonationModal(
            onDismiss = { showDonationModal = false },
            onSuccess = {
                // 기부 성공 시 데이터 새로고침
                viewModel.loadDonationSummary()
                viewModel.loadDonationStatus(uiState.currentStatusType)
            }
        )
    }
}

@Composable
private fun DonationStatusContent(
    donationSummary: DonationSummary?,
    donationStatus: DonationStatus?,
    currentStatusType: DonationStatusType,
    onStatusTypeChanged: (DonationStatusType) -> Unit,
    onDonateClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String? = null
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // 헤더 메시지
        item {
            Text(
                text = "작은 티끌이 만든 변화를 확인해보세요",
                fontSize = 14.sp,
                color = TiggleGrayText,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }

        // 기부 요약 카드
        item {
            if (donationSummary != null) {
                SummaryCard(summary = donationSummary)
            } else if (!errorMessage.isNullOrEmpty()) {
                SummaryErrorCard(errorMessage = errorMessage)
            } else {
                // 로딩 중이거나 데이터가 없는 경우 기본 카드 표시
                SummaryLoadingCard()
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }

        // 탭 버튼들
        item {
            StatusTypeButtons(
                currentType = currentStatusType,
                onTypeChanged = onStatusTypeChanged
            )
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // 기부 현황 타이틀
        item {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 파란색 세로 라인
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(20.dp)
                        .background(
                            color = TiggleBlue,
                            shape = RoundedCornerShape(2.dp)
                        )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = getStatusTitle(currentStatusType),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // 기부 현황 리스트
        item {
            if (isLoading) {
                DonationStatusLoadingCard()
            } else if (donationStatus != null) {
                DonationStatusList(status = donationStatus)
            } else if (!errorMessage.isNullOrEmpty()) {
                DonationStatusErrorCard(errorMessage = errorMessage)
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        // 하단 콘텐츠 (탭별로 다름)
        item {
            if (donationStatus != null && !isLoading) {
                val totalAmount =
                    donationStatus.planetAmount + donationStatus.peopleAmount + donationStatus.prosperityAmount

                when (currentStatusType) {
                    DonationStatusType.ALL_UNIVERSITY -> {
                        TotalAmountCard(
                            title = "전체 학교 총 기부액",
                            totalAmount = totalAmount
                        )
                    }

                    DonationStatusType.UNIVERSITY -> {
                        TotalAmountCard(
                            title = "우리 학교 총 기부액",
                            totalAmount = totalAmount
                        )
                    }

                    DonationStatusType.MY_DONATION -> {
                        TiggleButton(
                            onClick = onDonateClick,
                            text = "기부하기",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .height(48.dp),
                        )
                    }
                }
            }
        }

        // 하단 여백 추가
        item { Spacer(modifier = Modifier.height(32.dp)) }
    }
}

@Composable
private fun SummaryCard(summary: DonationSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "${summary.totalAmount}원",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = "내 총 기부 금액",
                fontSize = 14.sp,
                color = TiggleGrayText,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SummaryItem(
                    value = "${summary.monthlyAmount}원",
                    label = "이번 달"
                )
                SummaryItem(
                    value = "${summary.categoryCnt}개",
                    label = "참여 분야"
                )
                SummaryItem(
                    value = "${summary.universityRank}위",
                    label = "학교 순위"
                )
            }
        }
    }
}

@Composable
private fun SummaryErrorCard(errorMessage: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "기부 요약 정보를 불러올 수 없습니다",
                    fontSize = 14.sp,
                    color = TiggleGrayText,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "다시 시도해주세요",
                    fontSize = 12.sp,
                    color = TiggleGrayText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun SummaryLoadingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = TiggleBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "기부 요약 정보를 불러오는 중...",
                    fontSize = 14.sp,
                    color = TiggleGrayText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DonationStatusErrorCard(errorMessage: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "⚠️",
                    fontSize = 32.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "기부 현황을 불러올 수 없습니다",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "네트워크 연결을 확인하고\n다시 시도해주세요",
                    fontSize = 14.sp,
                    color = TiggleGrayText,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = TiggleGrayText,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun StatusTypeButtons(
    currentType: DonationStatusType,
    onTypeChanged: (DonationStatusType) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = TiggleGrayLight),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            StatusTab(
                text = "나의 기부",
                isSelected = currentType == DonationStatusType.MY_DONATION,
                onClick = { onTypeChanged(DonationStatusType.MY_DONATION) },
                modifier = Modifier.weight(1f)
            )
            StatusTab(
                text = "우리 학교",
                isSelected = currentType == DonationStatusType.UNIVERSITY,
                onClick = { onTypeChanged(DonationStatusType.UNIVERSITY) },
                modifier = Modifier.weight(1f)
            )
            StatusTab(
                text = "전체 학교",
                isSelected = currentType == DonationStatusType.ALL_UNIVERSITY,
                onClick = { onTypeChanged(DonationStatusType.ALL_UNIVERSITY) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatusTab(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(21.dp))
            .background(
                if (isSelected) TiggleBlue else Color.Transparent
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else TiggleGrayText,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DonationStatusList(status: DonationStatus) {
    // 총 금액 계산 (진행률 계산을 위해)
    val totalAmount = status.planetAmount + status.peopleAmount + status.prosperityAmount

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            DonationProgressItem(
                category = DonationCategory.PLANET,
                amount = status.planetAmount,
                totalAmount = totalAmount,
                delayMillis = 0
            )
            DonationProgressItem(
                category = DonationCategory.PEOPLE,
                amount = status.peopleAmount,
                totalAmount = totalAmount,
                delayMillis = 200
            )
            DonationProgressItem(
                category = DonationCategory.PROSPERITY,
                amount = status.prosperityAmount,
                totalAmount = totalAmount,
                delayMillis = 400
            )
        }
    }
}

@Composable
private fun DonationStatusLoadingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp), // 실제 카드와 비슷한 높이
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = TiggleBlue,
                    modifier = Modifier.size(40.dp),
                    strokeWidth = 4.dp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "기부 현황을 불러오는 중...",
                    fontSize = 14.sp,
                    color = TiggleGrayText
                )
            }
        }
    }
}

@Composable
private fun DonationProgressItem(
    category: DonationCategory,
    amount: Int,
    totalAmount: Int,
    delayMillis: Int
) {
    var isVisible by remember { mutableStateOf(false) }

    // 진행률 계산 (최대 1.0)
    val progress = if (totalAmount > 0) amount.toFloat() / totalAmount.toFloat() else 0f

    // 애니메이션된 진행률
    val animatedProgress by animateFloatAsState(
        targetValue = if (isVisible) progress else 0f,
        animationSpec = tween(
            durationMillis = 1200,
            delayMillis = delayMillis,
            easing = FastOutSlowInEasing
        ),
        label = "progress"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Column {
        // 카테고리 아이콘과 이름
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                    painter = painterResource(id = getCategoryIconRes(category)),
                    contentDescription = category.value,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 카테고리 이름
            Text(
                text = getCategoryDisplayName(category),
                fontSize = 16.sp,
                color = TiggleGrayText,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 프로그레스 바
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(
                    width = 1.dp,
                    color = Color.Gray.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(20.dp)
                )
                .background(Color.Gray.copy(alpha = 0.1f))
        ) {
            // 진행률 바
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .clip(RoundedCornerShape(20.dp))
                    .background(getCategoryColor(category))
            )

            // 금액 텍스트 (중앙에 위치)
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = Formatter.formatCurrency(amount.toLong()),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TotalAmountCard(title: String, totalAmount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TiggleGrayLight)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            Text(
                text = Formatter.formatCurrency(totalAmount.toLong()),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
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

private fun getCategoryDisplayName(category: DonationCategory): String {
    return when (category) {
        DonationCategory.PLANET -> "Planet"
        DonationCategory.PEOPLE -> "People"
        DonationCategory.PROSPERITY -> "Prosperity"
    }
}

private fun getCategoryColor(category: DonationCategory): Color {
    return when (category) {
        DonationCategory.PLANET -> Color(0xFF4CAF50)  // 초록
        DonationCategory.PEOPLE -> Color(0xFFFF9800)  // 주황
        DonationCategory.PROSPERITY -> Color(0xFF9C27B0)  // 보라
    }
}

private fun getStatusTitle(type: DonationStatusType): String {
    return when (type) {
        DonationStatusType.MY_DONATION -> "나의 기부 현황"
        DonationStatusType.UNIVERSITY -> "우리 학교 기부 현황"
        DonationStatusType.ALL_UNIVERSITY -> "전체 학교 기부 현황"
    }
}

// 전체 학교 기부 현황 (기본)
@Preview(showBackground = true, name = "전체 학교 기부 현황")
@Composable
private fun DonationStatusAllUniversityPreview() {
    val sampleSummary = DonationSummary(
        totalAmount = 847,
        monthlyAmount = 330,
        categoryCnt = 3,
        universityRank = 53
    )

    val sampleStatus = DonationStatus(
        planetAmount = 1904400,
        peopleAmount = 238394,
        prosperityAmount = 530926
    )

    DonationStatusContent(
        donationSummary = sampleSummary,
        donationStatus = sampleStatus,
        currentStatusType = DonationStatusType.ALL_UNIVERSITY,
        onStatusTypeChanged = {},
        onDonateClick = {},
        isLoading = false,
        errorMessage = null
    )
}

// 우리 학교 기부 현황
@Preview(showBackground = true, name = "우리 학교 기부 현황")
@Composable
private fun DonationStatusUniversityPreview() {
    val sampleSummary = DonationSummary(
        totalAmount = 2450,
        monthlyAmount = 780,
        categoryCnt = 2,
        universityRank = 12
    )

    val sampleStatus = DonationStatus(
        planetAmount = 450000,
        peopleAmount = 120000,
        prosperityAmount = 280000
    )

    DonationStatusContent(
        donationSummary = sampleSummary,
        donationStatus = sampleStatus,
        currentStatusType = DonationStatusType.UNIVERSITY,
        onStatusTypeChanged = {},
        onDonateClick = {},
        isLoading = false,
        errorMessage = null
    )
}

// 나의 기부 현황
@Preview(showBackground = true, name = "나의 기부 현황")
@Composable
private fun DonationStatusMyDonationPreview() {
    val sampleSummary = DonationSummary(
        totalAmount = 847,
        monthlyAmount = 330,
        categoryCnt = 3,
        universityRank = 53
    )

    val sampleStatus = DonationStatus(
        planetAmount = 500,
        peopleAmount = 200,
        prosperityAmount = 147
    )

    DonationStatusContent(
        donationSummary = sampleSummary,
        donationStatus = sampleStatus,
        currentStatusType = DonationStatusType.MY_DONATION,
        onStatusTypeChanged = {},
        onDonateClick = {},
        isLoading = false
    )
}

// 로딩 상태
@Preview(showBackground = true, name = "로딩 상태")
@Composable
private fun DonationStatusLoadingPreview() {
    val sampleSummary = DonationSummary(
        totalAmount = 847,
        monthlyAmount = 330,
        categoryCnt = 3,
        universityRank = 53
    )

    DonationStatusContent(
        donationSummary = sampleSummary,
        donationStatus = null,
        currentStatusType = DonationStatusType.ALL_UNIVERSITY,
        onStatusTypeChanged = {},
        onDonateClick = {},
        isLoading = true
    )
}

// 기부 기록이 없는 경우
@Preview(showBackground = true, name = "기부 기록 없음")
@Composable
private fun DonationStatusEmptyPreview() {
    val sampleSummary = DonationSummary(
        totalAmount = 0,
        monthlyAmount = 0,
        categoryCnt = 0,
        universityRank = 999
    )

    val sampleStatus = DonationStatus(
        planetAmount = 0,
        peopleAmount = 0,
        prosperityAmount = 0
    )

    DonationStatusContent(
        donationSummary = sampleSummary,
        donationStatus = sampleStatus,
        currentStatusType = DonationStatusType.MY_DONATION,
        onStatusTypeChanged = {},
        onDonateClick = {},
        isLoading = false
    )
}

// 한 카테고리만 기부한 경우
@Preview(showBackground = true, name = "단일 카테고리 기부")
@Composable
private fun DonationStatusSingleCategoryPreview() {
    val sampleSummary = DonationSummary(
        totalAmount = 1500,
        monthlyAmount = 500,
        categoryCnt = 1,
        universityRank = 25
    )

    val sampleStatus = DonationStatus(
        planetAmount = 1500,
        peopleAmount = 0,
        prosperityAmount = 0
    )

    DonationStatusContent(
        donationSummary = sampleSummary,
        donationStatus = sampleStatus,
        currentStatusType = DonationStatusType.MY_DONATION,
        onStatusTypeChanged = {},
        onDonateClick = {},
        isLoading = false
    )
}

// 큰 금액의 기부 현황
@Preview(showBackground = true, name = "대규모 기부 현황")
@Composable
private fun DonationStatusLargeAmountPreview() {
    val sampleSummary = DonationSummary(
        totalAmount = 50000,
        monthlyAmount = 15000,
        categoryCnt = 3,
        universityRank = 3
    )

    val sampleStatus = DonationStatus(
        planetAmount = 25000000,
        peopleAmount = 15000000,
        prosperityAmount = 35000000
    )

    DonationStatusContent(
        donationSummary = sampleSummary,
        donationStatus = sampleStatus,
        currentStatusType = DonationStatusType.ALL_UNIVERSITY,
        onStatusTypeChanged = {},
        isLoading = false,
        errorMessage = null,
        onDonateClick = {}
    )
}

// 오류 상태
@Preview(showBackground = true, name = "오류 상태")
@Composable
private fun DonationStatusErrorPreview() {
    DonationStatusContent(
        donationSummary = null,
        donationStatus = null,
        currentStatusType = DonationStatusType.ALL_UNIVERSITY,
        onStatusTypeChanged = {},
        onDonateClick = {},
        isLoading = false,
        errorMessage = "기부 요약 정보를 불러오는 중 오류가 발생했습니다."
    )
}
