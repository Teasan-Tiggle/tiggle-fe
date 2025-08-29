package com.ssafy.tiggle.presentation.ui.piggybank

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ssafy.tiggle.R
import com.ssafy.tiggle.core.utils.Formatter
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.components.TiggleSwitchRow
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGray
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayLight
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText

@Composable
fun PiggyBankScreen(
    modifier: Modifier = Modifier,
    onOpenAccountClick: () -> Unit = {},
    onRegisterAccountClick: () -> Unit = {},
    onStartDutchPayClick: () -> Unit = {},
    onDutchPayStatusClick: () -> Unit = {},
    onAccountClick: (String) -> Unit = {},
    onShowPiggyBankDetailClick: () -> Unit = {},
    onEditLinkedAccountClick: () -> Unit = {},
    viewModel: PiggyBankViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // 최초 1회 로드
    LaunchedEffect(Unit) {
        viewModel.setPiggyBankAccount()
        viewModel.setMainAccount()
    }

    //다시 화면으로 돌아왔을 때(ON_RESUME) 갱신
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.setPiggyBankAccount()
                viewModel.setMainAccount()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    TiggleScreenLayout(
        showBackButton = false,
        showLogo = false,
        enableScroll = true,
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Spacer(Modifier.height(30.dp))
        Text(
            text = "티끌 저금통",
            color = Color.Black,
            fontSize = 22.sp,
            style = AppTypography.headlineLarge
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "작은 돈도 모으면 큰 힘이 됩니다",
            color = TiggleGrayText,
            fontSize = 13.sp,
            style = AppTypography.bodySmall
        )

        Spacer(Modifier.height(50.dp))

        if (uiState.hasPiggyBank) {
            TodaySavingBanner(uiState = uiState, onClick = onShowPiggyBankDetailClick)
        } else {
            DottedActionCard(
                title = "티끌 저금통 개설",
                desc = "계좌를 개설해\n티끌 저금통을 채워보세요!",
                onClick = onOpenAccountClick
            )
        }

        Spacer(Modifier.height(10.dp))

        if (uiState.hasLinkedAccount) {
            AccountCard(
                uiState = uiState, onClick = { accountNo ->
                    onAccountClick(accountNo)
                },
                onEditClick = { onEditLinkedAccountClick() })
        } else {
            DottedActionCard(
                title = "내 계좌 등록",
                desc = "나의 계좌를 등록하면\n티끌 저금통에 잔돈이 자동으로 기부됩니다.",
                onClick = onRegisterAccountClick
            )
        }

        Spacer(Modifier.height(16.dp))

        if (uiState.hasPiggyBank) {
            DutchButtonsRow(
                onStatus = onDutchPayStatusClick,
                onStart = onStartDutchPayClick
            )
        }

        Spacer(Modifier.height(20.dp))

        TiggleSwitchRow(
            title = "저금통 자동 기부",
            subtitle = "일정 금액의 티끌이 쌓이면 \n기부 단체에 자동으로 기부됩니다.",
            checked = uiState.piggyBank.autoDonation,
            onCheckedChange = viewModel::onToggleAutoDonation
        )

        HorizontalDivider(
            color = TiggleGray,
            thickness = 0.5.dp,
            modifier = Modifier.padding(10.dp)
        )

        TiggleSwitchRow(
            title = "잔돈 자동 저금",
            subtitle = "매일 자정에 1,000원 미만 잔돈을 자동으로 저금합니다.",
            checked = uiState.piggyBank.autoSaving,
            onCheckedChange = viewModel::onToggleAutoSaving
        )

        HorizontalDivider(
            color = TiggleGray,
            thickness = 0.5.dp,
            modifier = Modifier.padding(10.dp)
        )

        Spacer(Modifier.height(24.dp))

        if (uiState.showEsgCategorySheet) {
            EsgCategoryBottomSheet(
                show = uiState.showEsgCategorySheet,
                selectedId = uiState.piggyBank.esgCategory?.id,
                onPick = viewModel::onPickEsgCategory,
                onConfirm = viewModel::onConfirmAutoDonation,
                onDismiss = viewModel::onDismissEsgSheet
            )
        }
    }
}


@Composable
private fun DottedActionCard(
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    val radius = 12.dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawDottedRoundRect(radius)
            .clip(RoundedCornerShape(radius))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 원형 + 아이콘
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(TiggleGrayLight),
                contentAlignment = Alignment.Center
            ) {
                PlusIcon(color = TiggleGray)
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, color = Color.Black, fontSize = 15.sp, style = AppTypography.bodyLarge)
                Spacer(Modifier.height(3.dp))
                Text(
                    desc,
                    color = TiggleGrayText,
                    fontSize = 11.sp,
                    lineHeight = 18.sp,
                    style = AppTypography.bodySmall
                )
            }
        }
    }
}

// moved: replaced by TiggleSwitchRow in components

/** 점선 둥근 사각 보더 */
private fun Modifier.drawDottedRoundRect(cornerRadius: Dp) = this.then(
    Modifier
        .drawBehind {
            val strokeWidth = 1.dp.toPx()
            val r = cornerRadius.toPx()
            drawRoundRect(
                color = TiggleGray,
                style = Stroke(
                    width = strokeWidth,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                ),
                cornerRadius = CornerRadius(r, r)
            )
        }
)

/** 플랫 + 아이콘 */
@Composable
private fun PlusIcon(color: Color) {
    Canvas(modifier = Modifier.size(14.dp)) {
        val w = size.width
        val h = size.height
        val t = 3.0f
        drawLine(
            color,
            start = androidx.compose.ui.geometry.Offset(w / 2, 0f),
            end = androidx.compose.ui.geometry.Offset(w / 2, h),
            strokeWidth = t
        )
        drawLine(
            color,
            start = androidx.compose.ui.geometry.Offset(0f, h / 2),
            end = androidx.compose.ui.geometry.Offset(w, h / 2),
            strokeWidth = t
        )
    }
}

@Composable
private fun TodaySavingBanner(uiState: PiggyBankState, onClick: () -> Unit) {
    val radius = 18.dp
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(radius))
            .background(
                brush = Brush.horizontalGradient(
                    colorStops = arrayOf(
                        0.00f to TiggleBlue,
                        0.45f to Color(0xFF1F5AF4),
                        1.00f to Color(0xFFD2DDED)
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .clickable { onClick() }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = uiState.piggyBankAccount.name,
                    color = Color(0xCCFFFFFF),
                    style = AppTypography.bodySmall
                )
                Spacer(Modifier.height(6.dp))
                val target = uiState.piggyBankAccount.currentAmount   // Long

                AnimatedNumberCounter(
                    targetValue = target
                )
//                Text(
//                    text = "${Formatter.formatCurrency(animated.toLong())}원",
//                    color = Color.White,
//                    fontSize = 34.sp,
//                    fontWeight = FontWeight.ExtraBold
//                )

                Spacer(Modifier.height(10.dp))
                Text(
                    "+ 지난주에 ${
                        Formatter.formatCurrency(uiState.piggyBankAccount.lastWeekSavedAmount)
                    }이 저금 됐어요",
                    color = Color(0xE6FFFFFF),
                    style = AppTypography.bodySmall
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.Bottom)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.pig),
                    contentDescription = "돼지",
                    Modifier.size(80.dp)
                )
            }
        }
    }
}

@Composable
private fun AccountCard(
    uiState: PiggyBankState,
    onClick: (String) -> Unit,
    onEditClick: () -> Unit
) {
    val radius = 14.dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(radius))
            .background(Color.White)
            .border(1.dp, Color(0x11000000), RoundedCornerShape(radius))
            .padding(16.dp)
            .clickable { onClick(uiState.mainAccount.accountNo) }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shinhan),
                    contentDescription = "신한로고",
                    Modifier.size(30.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "${uiState.mainAccount.accountName}",
                    color = Color.Black,
                    style = AppTypography.bodyLarge,
                    fontSize = 15.sp
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    "신한 ${uiState.mainAccount.accountNo}",
                    color = TiggleGrayText,
                    style = AppTypography.bodySmall
                )
            }
            Box() {
                Image(
                    painter = painterResource(id = R.drawable.linked_card_option),
                    contentDescription = "옵션 버튼",
                    Modifier
                        .size(20.dp)
                        .clickable { onEditClick() }
                )
            }
        }
        Spacer(Modifier.height(17.dp))
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End) {
            Text("잔액", color = TiggleGrayText, style = AppTypography.bodySmall)
            Spacer(Modifier.height(5.dp))
            Text(
                "${formatAmount(uiState.mainAccount.balance.toLong())}원",
                color = Color.Black,
                fontSize = 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DutchButtonsRow(onStatus: () -> Unit, onStart: () -> Unit) {
    Spacer(Modifier.height(20.dp))
    Row(Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = onStatus,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, TiggleGray),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
        ) { Text("더치페이 현황", style = AppTypography.bodyLarge) }

        Spacer(Modifier.width(12.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TiggleBlue,
                contentColor = Color.White
            )
        ) { Text("더치페이 하기", style = AppTypography.bodyLarge) }
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
            easing = androidx.compose.animation.core.EaseOutQuart
        ),
        label = "number_animation"
    )

    Text(
        text = "${Formatter.formatCurrency(animatedValue.toLong())}",
        color = Color.White,
        fontSize = 34.sp,
        fontWeight = FontWeight.ExtraBold
    )
}

@Preview
@Composable
private fun Preview() {
    PiggyBankScreen()
}