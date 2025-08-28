package com.ssafy.tiggle.presentation.ui.dutchpay

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.tiggle.core.utils.Formatter
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayDetail
import com.ssafy.tiggle.domain.entity.dutchpay.Creator
import com.ssafy.tiggle.domain.entity.dutchpay.Share
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue

@Composable
fun DutchPayDetailScreen(
    dutchPayId: Long,
    onBackClick: () -> Unit,
    viewModel: DutchPayDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(dutchPayId) {
        viewModel.loadDutchPayDetail(dutchPayId)
    }

    TiggleScreenLayout(
        showBackButton = true,
        title = "더치페이 현황",
        onBackClick = onBackClick,
        enableScroll = true
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
                }
            }

            uiState.dutchPayDetail != null -> {
                DutchPayDetailContent(detail = uiState.dutchPayDetail!!)
            }
        }
    }
}

@Composable
private fun DutchPayDetailContent(detail: DutchPayDetail) {
    var selectedTab by remember { mutableStateOf("PENDING") } // 기본값은 정산 미완료
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 요약 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${detail.shares.size}명 참여",
                    style = AppTypography.bodyMedium,
                    color = Color(0xFF666666)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = Formatter.formatCurrency(detail.totalAmount.toLong()),
                    style = AppTypography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "요청일 ${Formatter.formatDate(detail.createdAt)}",
                    style = AppTypography.bodySmall,
                    color = Color(0xFF999999)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 상태 표시
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val completedCount = detail.shares.count { it.status == "PAID" }
                    val pendingCount = detail.shares.count { it.status == "PENDING" }
                    
                    StatusItem(
                        count = completedCount,
                        label = "정산 완료",
                        backgroundColor = Color(0xFFE8F5E8)
                    )
                    
                    StatusItem(
                        count = pendingCount,
                        label = "정산 미완료",
                        backgroundColor = Color(0xFFF0F0F0)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 탭 (선택 가능)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            // 정산 완료 탭
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (selectedTab == "PAID") Color.White else Color.Transparent,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { selectedTab = "PAID" }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                val completedCount = detail.shares.count { it.status == "PAID" }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${completedCount}명",
                        style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = if (selectedTab == "PAID") TiggleBlue else Color(0xFF666666)
                    )
                    Text(
                        text = "정산 완료",
                        style = AppTypography.bodySmall,
                        color = if (selectedTab == "PAID") TiggleBlue else Color(0xFF999999)
                    )
                }
            }
            
            // 정산 미완료 탭
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (selectedTab == "PENDING") Color.White else Color.Transparent,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { selectedTab = "PENDING" }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                val pendingCount = detail.shares.count { it.status == "PENDING" }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${pendingCount}명",
                        style = AppTypography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = if (selectedTab == "PENDING") TiggleBlue else Color(0xFF666666)
                        )
                    )
                    Text(
                        text = "정산 미완료",
                        style = AppTypography.bodySmall,
                        color = if (selectedTab == "PENDING") TiggleBlue else Color(0xFF999999)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 참여자 목록 (선택된 탭에 따라 표시)
        val selectedShares = detail.shares.filter { it.status == selectedTab }
        
        if (selectedShares.isNotEmpty()) {
            selectedShares.forEach { share ->
                ParticipantItem(
                    name = share.name,
                    amount = share.amount.toLong(),
                    status = share.status
                )
                if (share != selectedShares.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = Color(0xFFE0E0E0)
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (selectedTab == "PAID") "정산 완료인 참여자가 없습니다" else "정산 미완료인 참여자가 없습니다",
                    style = AppTypography.bodyMedium,
                    color = Color(0xFF999999)
                )
            }
        }
    }
}

@Composable
private fun StatusItem(
    count: Int,
    label: String,
    backgroundColor: Color
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${count}명",
                style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = label,
                style = AppTypography.bodySmall,
                color = Color(0xFF666666)
            )
        }
    }
}

@Composable
private fun ParticipantItem(
    name: String,
    amount: Long,
    status: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = name,
                style = AppTypography.bodyMedium,
                color = Color.Black
            )
            
            // 상태 표시
            Box(
                modifier = Modifier
                    .background(
                        color = if (status == "PAID") Color(0xFFE8F5E8) else Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (status == "PAID") "완료" else "대기",
                    style = AppTypography.bodySmall,
                    color = if (status == "PAID") Color(0xFF2E7D32) else Color(0xFFF57C00)
                )
            }
        }
        
        Text(
            text = Formatter.formatCurrency(amount),
            style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DutchPayDetailScreenPreview() {
    val sampleDetail = DutchPayDetail(
        id = 2L,
        title = "치킨 회식",
        message = "치킨/맥주 더치페이 부탁!",
        totalAmount = 11111,
        status = "REQUESTED",
        creator = Creator(id = 1L, name = "김테스트"),
        shares = listOf(
            Share(userId = 1L, name = "김테스트", amount = 3703, status = "PAID"),
            Share(userId = 2L, name = "박테스트", amount = 3704, status = "PAID"),
            Share(userId = 10L, name = "jiwon", amount = 3704, status = "PENDING")
        ),
        roundedPerPerson = null,
        payMore = false,
        createdAt = "2025-08-28T12:46:16"
    )
    
    DutchPayDetailContent(detail = sampleDetail)
}
