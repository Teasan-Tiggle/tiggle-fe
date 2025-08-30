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
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
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
import android.util.Log
import androidx.compose.foundation.layout.size

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

    // payMore 상태를 detail에서 초기화 (한 번만)
    LaunchedEffect(uiState.dutchPayDetail) {
        uiState.dutchPayDetail?.let { detail ->
            if (payMoreEnabled == false) { // 초기값일 때만 설정
                payMoreEnabled = detail.payMoreDefault
                Log.d("DutchpayRecieveScreen", "payMoreEnabled 초기화: ${detail.payMoreDefault}")
            }
        }
    }

    // 송금 성공 시 처리 - 다이얼로그에서 처리하므로 여기서는 제거

    // 로딩 화면을 전체 화면 중앙에 표시
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    color = TiggleBlue,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "송금 중...",
                    style = AppTypography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    } else {
        TiggleScreenLayout(
            onBackClick = onBackClick,
            bottomButton = {
                uiState.dutchPayDetail?.let { detail ->
                    if (!detail.isCreator) {
                        TiggleButton(
                            text = "송금하기",
                            onClick = { viewModel.payDutchPay(dutchPayId, payMoreEnabled) },
                            enabled = true,
                            isLoading = false,
                            variant = TiggleButtonVariant.Primary
                        )
                    }
                }
            }
        ) {
            uiState.dutchPayDetail?.let { detail ->
                DutchPayPaymentContent(
                    detail = detail,
                    payMoreEnabled = payMoreEnabled,
                    onPayMoreChanged = { 
                        Log.d("DutchpayRecieveScreen", "payMoreEnabled 변경: $it")
                        payMoreEnabled = it 
                    }
                )
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
    
    // 송금 성공 다이얼로그 표시
    uiState.isPaymentSuccess?.let { isSuccess ->
        if (isSuccess) {
            PaymentSuccessDialog(
                onDismiss = {
                    viewModel.clearPaymentSuccess()
                    onPaymentClick()
                }
            )
        }
    }
}

@Composable
private fun AnimatedNumberCounter(
    targetValue: Long,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    val animatedValue by animateIntAsState(
        targetValue = if (isVisible) targetValue.toInt() else 0,
        animationSpec = tween(
            durationMillis = 1000,
            easing = androidx.compose.animation.core.LinearEasing
        ),
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
    // payMoreEnabled에 따른 tiggleAmount 계산
    val currentTiggleAmount = if (payMoreEnabled) detail.tiggleAmount else 0L
    
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

                if (payMoreEnabled && currentTiggleAmount > 0) {
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
                            targetValue = currentTiggleAmount
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))


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

@Composable
private fun PaymentSuccessDialog(
    onDismiss: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(com.ssafy.tiggle.R.raw.firework)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        ),
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Lottie 애니메이션
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.size(120.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "송금에 성공했습니다!",
                    style = AppTypography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Text(
                text = "더치페이 정산이 완료되었습니다.",
                style = AppTypography.bodyMedium,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            TiggleButton(
                text = "확인",
                onClick = onDismiss,
                enabled = true,
                isLoading = false,
                variant = TiggleButtonVariant.Primary
            )
        }
    )
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
