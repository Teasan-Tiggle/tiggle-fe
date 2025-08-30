package com.ssafy.tiggle.presentation.ui.dutchpay

import android.R.attr.label
import android.util.Log
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.tiggle.R
import com.ssafy.tiggle.core.utils.Formatter
import com.ssafy.tiggle.domain.entity.dutchpay.UserSummary
import com.ssafy.tiggle.presentation.ui.components.TiggleButton
import com.ssafy.tiggle.presentation.ui.components.TiggleButtonVariant
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.components.TiggleSwitchRow
import com.ssafy.tiggle.presentation.ui.components.TiggleTextField
import com.ssafy.tiggle.presentation.ui.components.UserPicker
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
import com.ssafy.tiggle.presentation.ui.dutchpay.CreateDutchPayState
import com.ssafy.tiggle.presentation.ui.dutchpay.CreateDutchPayStep
import com.ssafy.tiggle.presentation.ui.dutchpay.CreateDutchPayViewModel
import kotlin.math.ceil

@Composable
fun CreateDutchPayScreen(
    onBackClick: () -> Unit = {},
    onFinish: () -> Unit = {},
    viewModel: CreateDutchPayViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
    }

    TiggleScreenLayout(
        title = when (uiState.step) {
            CreateDutchPayStep.PICK_USERS -> "유저 선택"
            CreateDutchPayStep.INPUT_AMOUNT -> "결제 금액 입력"
            CreateDutchPayStep.COMPLETE -> "요청 전송 완료"
        },
        onBackClick = {
            if (uiState.step == CreateDutchPayStep.PICK_USERS) onBackClick() else viewModel.goPrev()
        },
        enableScroll = when (uiState.step) {
            CreateDutchPayStep.PICK_USERS -> false
            CreateDutchPayStep.INPUT_AMOUNT -> true
            CreateDutchPayStep.COMPLETE -> true
        },
        bottomButton = {
            TiggleButton(
                text = when (uiState.step) {
                    CreateDutchPayStep.PICK_USERS -> "다음"
                    CreateDutchPayStep.INPUT_AMOUNT -> "요청 보내기"
                    CreateDutchPayStep.COMPLETE -> "완료"
                },
                onClick = {
                    when (uiState.step) {
                        CreateDutchPayStep.PICK_USERS -> viewModel.goNext()
                        CreateDutchPayStep.INPUT_AMOUNT -> viewModel.goNext()
                        CreateDutchPayStep.COMPLETE -> onFinish()
                    }
                },
                enabled = when (uiState.step) {
                    CreateDutchPayStep.PICK_USERS -> uiState.selectedUserIds.isNotEmpty() && !uiState.isLoading
                    CreateDutchPayStep.INPUT_AMOUNT -> uiState.amountText.isNotBlank() && uiState.title.isNotBlank() && !uiState.isLoading
                    CreateDutchPayStep.COMPLETE -> !uiState.isLoading
                },
                isLoading = uiState.isLoading,
                variant = TiggleButtonVariant.Primary
            )
        }
    ) {
        when (uiState.step) {
            CreateDutchPayStep.PICK_USERS -> {
                DutchPayPickUsersContent(
                    users = uiState.users,
                    selectedUserIds = uiState.selectedUserIds,
                    onToggleUser = viewModel::toggleUser
                )
            }

            CreateDutchPayStep.INPUT_AMOUNT -> {
                DutchPayInputAmountContent(
                    selectedUsers = uiState.users.filter { uiState.selectedUserIds.contains(it.id) },
                    amountText = uiState.amountText,
                    onAmountChange = viewModel::updateAmount,
                    selectedCount = uiState.selectedUserIds.size,
                    payMore = uiState.payMore,
                    onPayMoreChange = viewModel::setPayMore,
                    title = uiState.title,
                    onTitleChange = viewModel::updateTitle,
                    message = uiState.message,
                    onMessageChange = viewModel::updateMessage
                )
            }

            CreateDutchPayStep.COMPLETE -> {
                DutchPayCompleteContent(
                    totalAmount = uiState.amountText.toLongOrNull() ?: 0L,
                    selectedUsers = uiState.users.filter { uiState.selectedUserIds.contains(it.id) },
                    payMore = uiState.payMore
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
}

@Composable
fun DutchPayPickUsersContent(
    users: List<UserSummary>,
    selectedUserIds: Set<Long>,
    onToggleUser: (Long) -> Unit
) {
    Text("함께 결제할 유저를 선택하세요", style = AppTypography.bodyLarge)
    Spacer(Modifier.height(12.dp))
    UserPicker(
        users = users,
        selectedUserIds = selectedUserIds,
        onToggleUser = onToggleUser
    )
}

@Composable
fun DutchPayInputAmountContent(
    selectedUsers: List<UserSummary>,
    amountText: String,
    onAmountChange: (String) -> Unit,
    selectedCount: Int,
    payMore: Boolean,
    onPayMoreChange: (Boolean) -> Unit,
    title: String,
    onTitleChange: (String) -> Unit,
    message: String,
    onMessageChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp)
    ) {
        // 선택한 친구 섹션
        if (selectedUsers.isNotEmpty()) {
            Text("선택한 친구 (${selectedUsers.size}명)", style = AppTypography.bodyLarge)
            Spacer(Modifier.height(8.dp))
            FlowRow(modifier = Modifier.fillMaxWidth()) {
                selectedUsers.forEach { user ->
                    SelectedUserBadge(name = user.name)
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // 더치페이 제목
        Spacer(Modifier.height(12.dp))

        AmountInputCard(
            value = amountText,
            onValueChange = onAmountChange
        )
        Spacer(Modifier.height(16.dp))
        // 내가 더 낼게요 스위치
        TiggleSwitchRow(
            title = "내가 더 낼게요",
            subtitle = "자투리 금액을 티끌 저금통에 적립",
            checked = payMore,
            onCheckedChange = onPayMoreChange
        )
        
        // 티끌 적립 정보 표시 (payMore가 true일 때만)
        if (payMore) {
            val total = amountText.toLongOrNull() ?: 0L
            val participantCount = if (selectedCount > 0) selectedCount + 1 else 0
            if (total > 0 && participantCount > 0) {
                val perHead = total.toDouble() / participantCount
                val myAmount = roundUpToHundreds(perHead)
                val tiggleAmount = myAmount - perHead.toLong()
                
                if (tiggleAmount > 0) {
                    Spacer(Modifier.height(12.dp))
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
                                targetValue = tiggleAmount
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        TiggleTextField(
            value = title,
            onValueChange = onTitleChange,
            label = "더치페이 제목",
            placeholder = "예: 어제 먹은 치킨 정산"
        )



        Spacer(Modifier.height(16.dp))

        // 전달 메시지
        TiggleTextField(
            value = message,
            onValueChange = onMessageChange,
            label = "전달 메시지",
            placeholder = "친구들에게 전달할 메시지를 입력해주세요\n(예: 오늘 치킨 먹은 금액입니다!)",

            maxLines = 3,
            minLines = 3
        )

        // 결제 요약 (입력값과 선택 인원 있을 때만 표시)
        val total = amountText.toLongOrNull() ?: 0L
        val participantCount = if (selectedCount > 0) selectedCount + 1 else 0
        if (total > 0 && participantCount > 0) {
            Spacer(Modifier.height(16.dp))
            DutchPaySummary(
                totalAmount = total,
                participantCount = participantCount,
                payMore = payMore
            )
        }
    }
}

@Composable
private fun SelectedUserBadge(name: String) {
    Row(
        modifier = Modifier
            .padding(end = 8.dp, bottom = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(TiggleGrayLight)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, style = AppTypography.bodySmall)
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
private fun DutchPaySummary(totalAmount: Long, participantCount: Int, payMore: Boolean) {
    // 1인당 금액 계산
    val perHead = if (participantCount > 0) totalAmount.toDouble() / participantCount else 0.0
    val myAmount = if (payMore) roundUpToHundreds(perHead) else perHead.toLong()

    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("결제 요약", style = AppTypography.bodyLarge)
            Spacer(Modifier.height(12.dp))
            SummaryRow(label = "총 금액", value = Formatter.formatCurrency(totalAmount))

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(Modifier.height(8.dp))

            SummaryRow(label = "참여 인원", value = "${participantCount}명(나 포함)")

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(Modifier.height(8.dp))
            SummaryRow(label = "1인당 금액", value = Formatter.formatCurrency(perHead.toLong()))
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
            Spacer(Modifier.height(8.dp))
            SummaryRow(label = "내 결제 금액", value = Formatter.formatCurrency(myAmount))
            
            // 티끌 적립 정보 표시 (payMore가 true일 때만)
            if (payMore) {
                val tiggleAmount = myAmount - perHead.toLong()
                if (tiggleAmount > 0) {
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🐷 티끌 적립",
                            style = AppTypography.bodySmall,
                            color = TiggleBlue
                        )
                        AnimatedNumberCounter(
                            targetValue = tiggleAmount
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = AppTypography.bodySmall)
        Text(text = value, style = AppTypography.bodySmall)
    }
}

private fun roundUpToHundreds(value: Double): Long {
    // 100원 단위 올림
    val asLong = ceil(value / 100.0) * 100.0
    return asLong.toLong()
}

// Formatter를 사용하므로 로컬 함수는 제거

@Composable
fun DutchPayCompleteContent(
    totalAmount: Long = 50000L,
    selectedUsers: List<UserSummary> = emptyList(),
    payMore: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        // 아이콘
        Box(
            modifier = Modifier
                .size(150.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.wallet),
                contentDescription = "Wallet",
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(Modifier.height(24.dp))

        // 제목
        Text(
            text = "더치페이 요청 완료",
            style = AppTypography.headlineMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        // 설명
        Text(
            text = "친구들에게 더치페이 요청을 보냈습니다.",
            style = AppTypography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))

        // 요약 정보 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "요청 금액",
                    style = AppTypography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = Formatter.formatCurrency(totalAmount),
                    style = AppTypography.headlineLarge.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(Modifier.height(16.dp))

                HorizontalDivider(color = Color.Gray.copy(alpha = 0.2f))

                Spacer(Modifier.height(16.dp))

                // 참여자 정보
                val participantCount = selectedUsers.size + 1 // 나 포함
                val perHead =
                    if (participantCount > 0) totalAmount.toDouble() / participantCount else 0.0
                val myAmount = if (payMore) roundUpToHundreds(perHead) else perHead.toLong()
                val friendAmount = perHead.toLong()



                Text("참여자 (${participantCount}명)")

                DetailRow(
                    label = "내가 낸 금액",
                    value = Formatter.formatCurrency(myAmount)
                )

                if (selectedUsers.isNotEmpty()) {
                    selectedUsers.forEach { user ->
                        Spacer(Modifier.height(12.dp))
                        DetailRow(
                            label = user.name,
                            value = Formatter.formatCurrency(friendAmount)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // 안내 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = TiggleBlue.copy(alpha = 0.1f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "💰",
                        style = AppTypography.bodyMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = "다음 단계",
                        style = AppTypography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TiggleBlue
                        )
                    )
                }

                Spacer(Modifier.height(8.dp))

                InfoStep(text = "친구들이 더치페이 요청을 확인합니다.")
                InfoStep(text = "각자 승인 후 결제를 진행합니다.")
                InfoStep(text = "모든 승인이 완료되면 정산이 진행됩니다.")
            }
        }

        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AppTypography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = AppTypography.bodyMedium.copy(
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun InfoStep(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "📋",
            style = AppTypography.bodySmall,
            modifier = Modifier.padding(end = 8.dp, top = 2.dp)
        )
        Text(
            text = text,
            style = AppTypography.bodySmall,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDutchPay_PickUsers() {
    val sampleUsers = listOf(
        UserSummary(4, "김테스트", "신은대학교", "컴퓨터학부"),
        UserSummary(5, "박테스트", "신한대학교", "건축학과"),
        UserSummary(6, "이테스트", "싸피대학교", "인공지능학과")
    )
    val uiState = CreateDutchPayState(
        step = CreateDutchPayStep.PICK_USERS,
        users = sampleUsers,
        selectedUserIds = setOf(4, 6)
    )

    TiggleScreenLayout(
        title = "유저 선택",
        onBackClick = {},
        bottomButton = {
            TiggleButton(
                text = "다음",
                onClick = {},
                enabled = uiState.selectedUserIds.isNotEmpty(),
                isLoading = false,
                variant = TiggleButtonVariant.Primary
            )
        }
    ) {
        DutchPayPickUsersContent(
            users = uiState.users,
            selectedUserIds = uiState.selectedUserIds,
            onToggleUser = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDutchPay_InputAmount() {
    val uiState = CreateDutchPayState(
        step = CreateDutchPayStep.INPUT_AMOUNT,
        amountText = "45000"
    )

    TiggleScreenLayout(
        title = "결제 금액 입력",
        onBackClick = {},
        bottomButton = {
            TiggleButton(
                text = "요청 보내기",
                onClick = {},
                enabled = uiState.amountText.isNotBlank(),
                isLoading = false,
                variant = TiggleButtonVariant.Primary
            )
        }
    ) {
        DutchPayInputAmountContent(
            selectedUsers = listOf(
                UserSummary(1, "김민호", "신은대학교", "컴퓨터학부"),
                UserSummary(2, "민경이", "신한대학교", "건축학과"),
                UserSummary(3, "홍길동", "싸피대학교", "인공지능학과")
            ),
            amountText = uiState.amountText,
            onAmountChange = {},
            selectedCount = 3,
            payMore = true,
            onPayMoreChange = {},
            title = "",
            onTitleChange = {},
            message = "",
            onMessageChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDutchPay_Complete() {
    TiggleScreenLayout(
        title = "요청 전송 완료",
        onBackClick = {},
        bottomButton = {
            TiggleButton(
                text = "완료",
                onClick = {},
                enabled = true,
                isLoading = false,
                variant = TiggleButtonVariant.Primary
            )
        }
    ) {
        DutchPayCompleteContent(
            totalAmount = 50000L,
            selectedUsers = listOf(
                UserSummary(1, "김민준", "신은대학교", "컴퓨터학부"),
                UserSummary(2, "박예준", "신한대학교", "건축학과")
            ),
            payMore = true
        )
    }
}

@Composable
private fun AmountInputCard(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "총 결제 금액",
                    style = AppTypography.bodyMedium,
                    color = Color.Gray
                )

                Spacer(Modifier.height(8.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = "50,000",
                            style = AppTypography.headlineLarge.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.Gray.copy(alpha = 0.4f)
                        )
                    }

                    BasicTextField(
                        value = if (value.isNotEmpty()) Formatter.formatCurrency(
                            value.toLongOrNull() ?: 0L
                        ) else "",
                        onValueChange = { text ->
                            val digitsOnly = text.filter { it.isDigit() }
                            onValueChange(digitsOnly)
                        },
                        textStyle = AppTypography.headlineLarge.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }


            }

        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = "숫자만 입력하세요 (쉼표 자동 추가)",
            style = AppTypography.bodySmall,
            color = Color.Gray
        )
    }

}
