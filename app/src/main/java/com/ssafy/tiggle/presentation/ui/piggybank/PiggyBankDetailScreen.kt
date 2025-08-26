package com.ssafy.tiggle.presentation.ui.piggybank

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.tiggle.R
import com.ssafy.tiggle.domain.entity.piggybank.PiggyBank
import com.ssafy.tiggle.domain.entity.piggybank.PiggyBankEntry
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlueLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText
import com.ssafy.tiggle.presentation.ui.theme.TiggleSkyBlue
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// ---------------------------
// Top-level Screen
// ---------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PiggyBankDetailsScreen(
    uiState: PiggyBankState,
    onBack: () -> Unit = {},
    onMore: () -> Unit = {},
    onTabChange: (PiggyTab) -> Unit = {},
    onItemClick: (PiggyBankEntry) -> Unit = {}
) {
    TiggleScreenLayout(
        showBackButton = true,
        title = "오늘 모인 티끌",
        onBackClick = onBack,
        contentPadding = PaddingValues(0.dp),
        topActions = {
            Row(horizontalArrangement = Arrangement.End) {
                Image(
                    painter = painterResource(id = R.drawable.linked_card_option),
                    contentDescription = "저금통 정보 수정",
                    modifier = Modifier.size(25.dp).clickable { onMore() }
                )
            }
        }
    ) {
        Column(Modifier.fillMaxSize()) {

            val today = remember { LocalDate.now() }
            SummaryHeader(
                amount = uiState.piggyBank.currentAmount,
                date = today,
                savedCount = uiState.piggyBank.savingCount,
                donationCount = uiState.piggyBank.donationCount,
                donationAmount = uiState.piggyBank.donationTotalAmount
            )

            Spacer(Modifier.height(8.dp))

            PiggyTabBar(
                selected = uiState.selectedTab,
                onSelected = onTabChange
            )

            Spacer(Modifier.height(12.dp))

            // --- 하단 고정 InfoTipCard + 상단 스크롤 리스트 ---
            when (uiState.selectedTab) {
                PiggyTab.SpareChange -> {
                    SectionTitle(text = "주간 자투리 적립")
                    Spacer(Modifier.height(10.dp))

                    Box(Modifier.fillMaxSize()) {
                        // 카드 높이만큼 하단 여백(패딩) 확보: 100~120dp 권장
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 108.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp)
                        ) {
                            items(uiState.changeList, key = { it.id }) { item ->
                                EntryItemCard(item = item, onClick = { onItemClick(item) })
                            }
                        }

                        InfoTipCard(
                            title = "자투리 적립 방식",
                            message = "매주 월요일에 내 계좌 잔액의 천원 미만 자투리 금액이 자동으로 저금통에 적립됩니다.",
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                        )
                    }
                }

                PiggyTab.DutchPay -> {
                    SectionTitle(text = "더치페이 잔돈 적립")
                    Spacer(Modifier.height(10.dp))

                    Box(Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 108.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp)
                        ) {
                            items(uiState.dutchpayList, key = { it.id }) { item ->
                                EntryItemCard(item = item, onClick = { onItemClick(item) })
                            }
                        }

                        InfoTipCard(
                            title = "더치페이 적립 방식",
                            message = "더치페이할 때 ‘내가 더 낼게요’를 선택하면 자투리 금액이 저금통에 적립됩니다.",
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(horizontal = 20.dp, vertical = 20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
        }
    }
}

// ---------------------------
// Components
// ---------------------------

@Composable
private fun SummaryHeader(
    amount: Long,
    date: LocalDate,
    savedCount: Int,
    donationCount: Int,
    donationAmount: Long
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(TiggleBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${formatAmount(amount)}원",
                fontSize = 44.sp,
                color = Color.White,
                style = AppTypography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                date.format(DateTimeFormatter.ofPattern("yyyy년 M월 d일")),
                color = Color.White.copy(alpha = 0.95f)
            )
            Spacer(Modifier.height(25.dp))
            StatPillRow(savedCount, donationCount, donationAmount)
        }
    }
}

@Composable
private fun StatPillRow(savedCount: Int, donationCount: Int, donationAmount: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.16f)),
        horizontalArrangement = Arrangement.spacedBy(13.dp, Alignment.CenterHorizontally)
    ) {
        StatPill(top = "${savedCount}회", bottom = "적립 횟수")
        StatPill(top = "${donationCount}회", bottom = "기부 횟수")
        StatPill(top = formatAmount(donationAmount), bottom = "기부 금액")
    }
}

@Composable
private fun StatPill(top: String, bottom: String) {
    Column(
        Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            top,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            style = AppTypography.bodyLarge
        )
        Spacer(Modifier.height(4.dp))
        Text(bottom, color = Color.White.copy(alpha = 0.95f), fontSize = 12.sp)
    }
}

@Composable
private fun PiggyTabBar(selected: PiggyTab, onSelected: (PiggyTab) -> Unit) {
    val items = PiggyTab.values()
    TabRow(
        selectedTabIndex = items.indexOf(selected),
        containerColor = Color.Transparent,
        contentColor = TiggleBlue,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.tabIndicatorOffset(tabPositions[items.indexOf(selected)]),
                height = 3.dp,
                color = TiggleBlue
            )
        }
    ) {
        items.forEach { tab ->
            Tab(
                selected = tab == selected,
                onClick = { onSelected(tab) },
                text = { Text(text = tab.label) },
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(20.dp)) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(20.dp)
                .background(TiggleBlue, RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(8.dp))
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
    }
}

@Composable
private fun EntryItemCard(item: PiggyBankEntry, onClick: () -> Unit) {
    val border = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    Card(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, border),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 아이콘 매핑
            val icon = when (item.type.lowercase()) {
                "자투리", "spare", "weekly", "change" -> R.drawable.coin_icon
                "더치페이", "dutch", "dutchpay" -> R.drawable.dutchpay_icon
                else -> null
            }

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (icon != null) Image(
                    painter = painterResource(icon),
                    contentDescription = null
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    item.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    item.occurredAt,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+ ${formatAmount(item.amount)}",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (item.type.lowercase() in listOf(
                            "자투리",
                            "spare",
                            "weekly",
                            "change"
                        )
                    ) "주간 적립" else "더치페이",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun InfoTipCard(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TiggleSkyBlue),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) { Text(text = "\uD83D\uDCA1") }

            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    color = TiggleBlueLight,
                    fontWeight = FontWeight.SemiBold,
                    style = AppTypography.bodyMedium
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = message,
                    color = TiggleGrayText,
                    lineHeight = 18.sp,
                    style = AppTypography.bodyMedium
                )
            }
        }
    }
}

// ---------------------------
// Preview
// ---------------------------

private fun sampleEntriesSpare() = listOf(
    PiggyBankEntry(
        id = "1",
        type = "CHANGE",
        amount = 600,
        occurredAt = "8월 18일 · 계좌 잔액 자투리",
        title = "8월의 4번째 자투리 적립"
    ),
    PiggyBankEntry(
        id = "2",
        type = "CHANGE",
        amount = 300,
        occurredAt = "8월 11일 · 계좌 잔액 자투리",
        title = "8월의 3번째 자투리 적립"
    )
)

private fun sampleEntriesDutch() = listOf(
    PiggyBankEntry(
        id = "3", type = "DUTCHPAY", amount = 50, occurredAt = "오후 10:30 · 4명 참여", title = "치킨 더치페이"
    ),
    PiggyBankEntry(
        id = "4",
        type = "DUTCHPAY",
        amount = 300,
        occurredAt = "오후 11:30 · 2명 참여(전우)",
        title = "택시 더치페이"
    )
)

@Preview(showBackground = true, widthDp = 360, name = "자투리 탭")
@Composable
fun PreviewPiggyBankDetails_Spare() {
    val state = PiggyBankState(
        piggyBank = PiggyBank(
            currentAmount = 847,
            savingCount = 5,
            donationCount = 3,
            donationTotalAmount = 500
        ),
        selectedTab = PiggyTab.SpareChange,
        changeList = sampleEntriesSpare(),
        dutchpayList = sampleEntriesDutch()
    )

    MaterialTheme {
        PiggyBankDetailsScreen(uiState = state)
    }
}

@Preview(showBackground = true, widthDp = 360, name = "더치페이 탭")
@Composable
fun PreviewPiggyBankDetails_Dutch() {
    val state = PiggyBankState(
        piggyBank = PiggyBank(
            currentAmount = 847,
            savingCount = 5,
            donationCount = 3,
            donationTotalAmount = 500
        ),
        selectedTab = PiggyTab.DutchPay,
        changeList = sampleEntriesSpare(),
        dutchpayList = sampleEntriesDutch()
    )

    MaterialTheme {
        PiggyBankDetailsScreen(uiState = state)
    }
}
