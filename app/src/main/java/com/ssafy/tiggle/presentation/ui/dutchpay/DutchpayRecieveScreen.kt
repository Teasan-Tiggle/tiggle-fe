package com.ssafy.tiggle.presentation.ui.dutchpay

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequestDetail
import com.ssafy.tiggle.presentation.ui.components.TiggleButton
import com.ssafy.tiggle.presentation.ui.components.TiggleButtonVariant
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.components.TiggleSwitchRow
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.dutchpay.DutchPayRequestDetailViewModel

@Composable
fun DutchpayRecieveScreen(
    dutchPayId: Long,
    onBackClick: () -> Unit = {},
    onPaymentClick: () -> Unit = {},
    viewModel: DutchPayRequestDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var payMoreEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(dutchPayId) {
        viewModel.loadDutchPayDetail(dutchPayId)
    }

    // payMore 상태를 detail에서 초기화
    LaunchedEffect(uiState.dutchPayDetail) {
        uiState.dutchPayDetail?.let { detail ->
            payMoreEnabled = detail.payMoreDefault
        }
    }

    // 송금 성공 시 처리
    LaunchedEffect(uiState.isPaymentSuccess) {
        if (uiState.isPaymentSuccess) {
            viewModel.clearPaymentSuccess()
            onPaymentClick()
        }
    }

    TiggleScreenLayout(
        onBackClick = onBackClick,
        bottomButton = {
            uiState.dutchPayDetail?.let { detail ->
                if (!detail.isCreator) {
                    TiggleButton(
                        text = "송금하기",
                        onClick = { viewModel.payDutchPay(dutchPayId, payMoreEnabled) },
                        enabled = !uiState.isLoading,
                        isLoading = uiState.isLoading,
                        variant = TiggleButtonVariant.Primary
                    )
                }
            }
        }
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

            else -> {
                uiState.dutchPayDetail?.let { detail ->
                    DutchPayPaymentContent(
                        detail = detail,
                        payMoreEnabled = payMoreEnabled,
                        onPayMoreChanged = { payMoreEnabled = it }
                    )
                }
            }
        }
    }

    // 에러 다이얼로그 표시
    uiState.errorMessage?.let { errorMessage ->
        AlertDialog(
            onDismissRequest = { viewModel.clearErrorMessage() },
            title = {
                Text("오류")
            },
            text = {
                Text(errorMessage)
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.clearErrorMessage() }
                ) {
                    Text("확인")
                }
            }
        )
    }
}

@Composable
private fun AnimatedNumberCounter(
    targetValue: Long,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateIntAsState(
        targetValue = targetValue.toInt(),
        animationSpec = tween(durationMillis = 1000),
        label = "number_animation"
    )
    
    Text(
        text = "+ ${Formatter.formatCurrency(animatedValue.toLong())}",
        style = AppTypography.bodyMedium.copy(fontWeight = FontWeight.Bold),
        color = TiggleBlue,
        modifier = modifier
    )
}

@Composable
private fun DutchPayPaymentContent(
    detail: DutchPayRequestDetail,
    payMoreEnabled: Boolean,
    onPayMoreChanged: (Boolean) -> Unit
) {
    // 내가 낼 금액 계산
    val myPaymentAmount = if (payMoreEnabled) {
        detail.originalAmount + detail.tiggleAmount
    } else {
        detail.originalAmount
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 요청자 정보
        Text(
            text = "${detail.requesterName}님이",
            style = AppTypography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Text(
            text = "더치페이를 요청했습니다",
            style = AppTypography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 전달된 메시지 (있을 경우)
        if (detail.message.isNotBlank()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📤 전달된 메시지",
                        style = AppTypography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = detail.message,
                        style = AppTypography.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 요청 정보
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "요청 정보",
                    style = AppTypography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(12.dp))

                DetailRow(label = "요청자", value = detail.requesterName)
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(label = "참여 인원", value = "${detail.participantCount}명")
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(label = "총 금액", value = Formatter.formatCurrency(detail.totalAmount))
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow(label = "요청 일시", value = Formatter.formatDateTime(detail.requestedAt))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 내가 내는 금액
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TiggleBlue.copy(alpha = 0.1f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "내가 내는 금액",
                    style = AppTypography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = Formatter.formatCurrency(myPaymentAmount),
                    style = AppTypography.headlineLarge.copy(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = TiggleBlue
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))

                Spacer(modifier = Modifier.height(12.dp))

                DetailRow(label = "원래 금액", value = Formatter.formatCurrency(detail.originalAmount))

                if (payMoreEnabled && detail.tiggleAmount > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "티끌",
                            style = AppTypography.bodyMedium,
                            color = Color.Gray
                        )
                        AnimatedNumberCounter(
                            targetValue = detail.tiggleAmount
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 티끌 적립 정보 표시 (payMoreEnabled이고 tiggleAmount가 0보다 클 때만)
        if (payMoreEnabled && detail.tiggleAmount > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F8FF)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🐷",
                        fontSize = 20.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "티끌 적립",
                        style = AppTypography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1B6BFF),
                        modifier = Modifier.weight(1f)
                    )
                    AnimatedNumberCounter(
                        targetValue = detail.tiggleAmount
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 돈 더내고 잔돈 기부하기 스위치
        TiggleSwitchRow(
            title = "돈 더내고 잔돈 기부하기",
            subtitle = "자투리 금액을 티끌 저금통에 적립",
            checked = payMoreEnabled,
            onCheckedChange = onPayMoreChanged
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Black
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = AppTypography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = AppTypography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            ),
            color = valueColor,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDutchPayPayment() {
    val sampleDetail = DutchPayRequestDetail(
        dutchPayId = 9L,
        title = "어제 먹은 치킨 정산",
        message = "오늘 치킨 먹은 금액입니다!",
        requesterName = "최지원",
        participantCount = 3,
        totalAmount = 50000L,
        requestedAt = "2025.08.20 14:32",
        myAmount = 17000L,
        originalAmount = 16666L,
        tiggleAmount = 334L,
        payMoreDefault = true,
        isCreator = false
    )

    TiggleScreenLayout(
        onBackClick = {},
        bottomButton = {
            TiggleButton(
                text = "송금하기",
                onClick = {},
                enabled = true,
                isLoading = false,
                variant = TiggleButtonVariant.Primary
            )
        }
    ) {
        DutchPayPaymentContent(detail = sampleDetail, payMoreEnabled = true, onPayMoreChanged = {})
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewNoTiggleDutchPayPayment() {
    val sampleDetail = DutchPayRequestDetail(
        dutchPayId = 9L,
        title = "어제 먹은 치킨 정산",
        message = "오늘 치킨 먹은 금액입니다!",
        requesterName = "최지원",
        participantCount = 3,
        totalAmount = 50000L,
        requestedAt = "2025.08.20 14:32",
        myAmount = 17000L,
        originalAmount = 16666L,
        tiggleAmount = 334L,
        payMoreDefault = false,
        isCreator = false
    )

    TiggleScreenLayout(
        onBackClick = {},
        bottomButton = {
            TiggleButton(
                text = "송금하기",
                onClick = {},
                enabled = true,
                isLoading = false,
                variant = TiggleButtonVariant.Primary
            )
        }
    ) {
        DutchPayPaymentContent(detail = sampleDetail, payMoreEnabled = false, onPayMoreChanged = {})
    }
}
