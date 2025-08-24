package com.ssafy.tiggle.presentation.ui.dutchpay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.tiggle.core.utils.Formatter
import com.ssafy.tiggle.domain.entity.UserSummary
import com.ssafy.tiggle.presentation.ui.components.TiggleButton
import com.ssafy.tiggle.presentation.ui.components.TiggleButtonVariant
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.components.TiggleSwitchRow
import com.ssafy.tiggle.presentation.ui.components.TiggleTextField
import com.ssafy.tiggle.presentation.ui.components.UserPicker
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
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
                    CreateDutchPayStep.PICK_USERS -> uiState.selectedUserIds.isNotEmpty()
                    CreateDutchPayStep.INPUT_AMOUNT -> uiState.amountText.isNotBlank()
                    CreateDutchPayStep.COMPLETE -> true
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
                    onPayMoreChange = viewModel::setPayMore
                )
            }

            CreateDutchPayStep.COMPLETE -> {
                DutchPayCompleteContent()
            }
        }
    }
}

@Composable
fun DutchPayPickUsersContent(
    users: List<UserSummary>,
    selectedUserIds: Set<Long>,
    onToggleUser: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp)
    ) {
        Text("함께 결제할 유저를 선택하세요", style = AppTypography.bodyLarge)
        Spacer(Modifier.height(12.dp))
        UserPicker(
            users = users,
            selectedUserIds = selectedUserIds,
            onToggleUser = onToggleUser
        )
    }
}

@Composable
fun DutchPayInputAmountContent(
    selectedUsers: List<UserSummary>,
    amountText: String,
    onAmountChange: (String) -> Unit,
    selectedCount: Int,
    payMore: Boolean,
    onPayMoreChange: (Boolean) -> Unit
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

        Text("총 결제 금액", style = AppTypography.bodyLarge)
        Spacer(Modifier.height(12.dp))
        TiggleTextField(
            value = amountText,
            onValueChange = onAmountChange,
            label = "총 결제 금액",
            placeholder = "예: 45000",
            keyboardType = KeyboardType.Number
        )

        Spacer(Modifier.height(16.dp))
        // 내가 더 낼게요 스위치
        TiggleSwitchRow(
            title = "내가 더 낼게요",
            subtitle = "자투리 금액을 티끌 저금통에 적립",
            checked = payMore,
            onCheckedChange = onPayMoreChange
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
        Column(modifier = Modifier.padding(16.dp)) {
            SummaryRow(label = "총 금액", value = Formatter.formatCurrency(totalAmount))
            Spacer(Modifier.height(8.dp))
            SummaryRow(label = "참여 인원", value = "${participantCount}명(나 포함)")
            Spacer(Modifier.height(8.dp))
            SummaryRow(label = "1인당 금액", value = Formatter.formatCurrency(perHead.toLong()))
            Spacer(Modifier.height(8.dp))
            SummaryRow(label = "내 결제 금액", value = Formatter.formatCurrency(myAmount))
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxSize(),
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
fun DutchPayCompleteContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 12.dp)
    ) {
        Text("요청을 보냈어요!", style = AppTypography.headlineLarge)
        Spacer(Modifier.height(8.dp))
        Text("상대가 승인하면 더치페이가 시작됩니다.")
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDutchPay_PickUsers() {
    val sampleUsers = listOf(
        UserSummary(4, "김테스트"),
        UserSummary(5, "박테스트"),
        UserSummary(6, "이테스트")
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
                UserSummary(1, "김민호"),
                UserSummary(2, "민경이"),
                UserSummary(3, "홍길동")
            ),
            amountText = uiState.amountText,
            onAmountChange = {},
            selectedCount = 3,
            payMore = true,
            onPayMoreChange = {}
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
        DutchPayCompleteContent()
    }
}
