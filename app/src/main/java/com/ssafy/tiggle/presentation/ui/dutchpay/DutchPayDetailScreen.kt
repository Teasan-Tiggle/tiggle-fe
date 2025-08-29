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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.ssafy.tiggle.presentation.ui.components.TiggleButton
import com.ssafy.tiggle.presentation.ui.components.TiggleButtonVariant
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue

@Composable
fun DutchPayDetailScreen(
    dutchPayId: Long,
    onBackClick: () -> Unit,
    onPaymentClick: () -> Unit = {},
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
                }
            }

            uiState.dutchPayDetail != null -> {
                DutchPayDetailContent(
                    detail = uiState.dutchPayDetail!!,
                    onPaymentClick = onPaymentClick
                )
            }
        }
    }
}

@Composable
private fun DutchPayDetailContent(
    detail: DutchPayDetail,
    onPaymentClick: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(1) } // 기본값은 정산 미완료 (인덱스 1)
    
    // 현재 사용자가 미정산 상태인지 확인 (PENDING 상태인 share가 있는지)
    val currentUserPendingShare = detail.shares.find { it.status == "PENDING" }
    
    // 탭 인덱스를 상태 문자열로 변환
    val selectedTab = if (selectedTabIndex == 0) "PAID" else "PENDING"
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 요약 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = TiggleBlue),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${detail.shares.size}명 참여",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = Formatter.formatCurrency(detail.totalAmount.toLong()),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "요청일 ${Formatter.formatDate(detail.createdAt)}",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
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
        val completedCount = detail.shares.count { it.status == "PAID" }
        val pendingCount = detail.shares.count { it.status == "PENDING" }
        
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${completedCount}명",
                            style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "정산 완료",
                            style = AppTypography.bodySmall
                        )
                    }
                }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${pendingCount}명",
                            style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                        )
                        Text(
                            text = "정산 미완료",
                            style = AppTypography.bodySmall
                        )
                    }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        

        
        // 참여자 목록 (선택된 탭에 따라 표시)
        val selectedShares = detail.shares.filter { it.status == selectedTab }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 8.dp)
        ) {
            if (selectedShares.isEmpty()) {
                item {
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
            } else {
                items(selectedShares, key = { it.userId }) { share ->
                    ParticipantItem(
                        name = share.name,
                        amount = share.amount.toLong(),
                        tiggleAmount = share.tiggleAmount?.toLong() ?: 0L,
                        status = share.status,
                        isCurrentUser = share.userId == detail.requestUserId, // requestUserId와 일치하는 사용자가 현재 사용자
                        onPaymentClick = onPaymentClick
                    )
                    
                    if (share != selectedShares.last()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFFE0E0E0)
                        )
                    }
                }
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
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        )
    )  {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${count}명",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = label,
                style = AppTypography.bodySmall,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ParticipantItem(
    name: String,
    amount: Long,
    tiggleAmount: Long,
    status: String,
    isCurrentUser: Boolean = false,
    onPaymentClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                
                // 상태 표시 (현재 사용자가 미정산 상태일 때는 정산하기 버튼, 그 외에는 상태 표시)
                if (isCurrentUser && status == "PENDING") {
                    Box(
                        modifier = Modifier
                            .background(
                                color = TiggleBlue.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { onPaymentClick() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "정산하기",
                            style = AppTypography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = TiggleBlue,
                            fontSize = 10.sp
                        )
                    }
                } else {
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
            }
            
            // 기본 금액
            Text(
                text = Formatter.formatCurrency(amount),
                style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Color.Black
            )
        }
        
        // tiggleAmount가 0이 아닐 때만 표시
        if (tiggleAmount > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🐷 티끌 적립",
                    style = AppTypography.bodySmall,
                    color = Color(0xFF666666)
                )
                Text(
                    text = "+ ${Formatter.formatCurrency(tiggleAmount)}",
                    style = AppTypography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = TiggleBlue
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            HorizontalDivider(color = Color(0xFFE0E0E0))
            Spacer(modifier = Modifier.height(4.dp))
            
            // 총 금액
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "정산 금액",
                    style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = Color.Black
                )
                Text(
                    text = Formatter.formatCurrency(amount + tiggleAmount),
                    style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = TiggleBlue
                )
            }
        }
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
            Share(userId = 1L, name = "김테스트", amount = 3703, tiggleAmount = 297, status = "PAID"),
            Share(userId = 2L, name = "박테스트", amount = 3704, tiggleAmount = 296, status = "PAID"),
            Share(userId = 10L, name = "jiwon", amount = 3704, tiggleAmount = 296, status = "PENDING")
        ),
        roundedPerPerson = null,
        payMore = false,
        createdAt = "2025-08-28T12:46:16",
        requestUserId = 10L // jiwon이 현재 사용자
    )
    
    DutchPayDetailContent(detail = sampleDetail, onPaymentClick = {})
}
