package com.ssafy.tiggle.presentation.ui.piggybank

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.tiggle.domain.entity.piggybank.DomainTransaction
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue

@Composable
fun MainAccountDetailScreen(
    accountNo: String,
    viewModel: PiggyBankViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    // 첫 진입 시 데이터 불러오기
    LaunchedEffect(Unit) {
        viewModel.loadTransactions(accountNo)
    }

    TiggleScreenLayout(
        showBackButton = true,
        title = "오늘 모인 티끌",
        onBackClick = onBackClick,
        contentPadding = PaddingValues(0.dp),
        enableScroll = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 상단 잔액 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TiggleBlue) // 파란색
                    .padding(0.dp, 70.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${formatAmount(state.mainAccountDetail.transactions.firstOrNull()?.balanceAfter?.toLong() ?: 0)}원",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "잔액",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // 거래 내역 리스트
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp, 0.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.mainAccountDetail.transactions) { tx ->
                    TransactionItem(tx)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(tx: DomainTransaction) {
    val borderColor = Color(0xFFE0E0E0) // 캡처 느낌의 연한 회색 테두리
    val timeTextColor = Color(0xFF9AA3AD) // 연한 회색 텍스트
    val amountColor = if (tx.transactionType == "출금") Color(0xFFD32F2F) else TiggleBlue

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(vertical = 10.dp, horizontal = 15.dp)) {

            // 상단 행: 날짜 + 이름 / 금액
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 날짜 + 거래처명
                Column {
                    Text(
                        text = formatMonthDay(tx.transactionDate), // "8.20" 형태
                        fontSize = 10.sp,
                        color = timeTextColor
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = tx.description,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1C1F23)
                    )
                }

                // 금액 (+/-)
                Text(
                    text = buildString {
                        append(if (tx.transactionType == "출금") "- " else "+ ")
                        append("${formatAmount(tx.amount.toLong())}원")
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = amountColor
                )
            }

            Spacer(Modifier.height(18.dp))

            // 하단 행: 시간 / 잔액
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tx.transactionTime,      // 예: "08:30"
                    fontSize = 10.sp,
                    color = timeTextColor
                )
                Text(
                    text = "${formatAmount(tx.balanceAfter.toLong())}원",
                    fontSize = 15.sp,
                    color = timeTextColor
                )
            }
        }
    }
}
