package com.ssafy.tiggle.presentation.ui.growth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ssafy.tiggle.R
import com.ssafy.tiggle.core.utils.Formatter
import com.ssafy.tiggle.presentation.ui.components.TiggleScreenLayout
import com.ssafy.tiggle.presentation.ui.theme.AppTypography
import com.ssafy.tiggle.presentation.ui.theme.TiggleBlue
import com.ssafy.tiggle.presentation.ui.theme.TiggleGrayText
import com.ssafy.tiggle.presentation.ui.theme.TiggleSkyBlue
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun GrowthScreen(
    modifier: Modifier = Modifier,
    onDonationHistoryClick: () -> Unit = {},
    onDonationStatusClick: () -> Unit = {},
    onDonationRankingClick: () -> Unit = {},
    viewModel: GrowthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    TiggleScreenLayout(
        showBackButton = false,
        showLogo = false,
        enableScroll = false
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            Spacer(Modifier.height(30.dp))

            Text(
                text = "나의 성장",
                color = Color.Black,
                fontSize = 22.sp,
                style = AppTypography.headlineLarge
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "작은 기부가 만든 변화를 확인해보세요",
                color = TiggleGrayText,
                fontSize = 13.sp,
                style = AppTypography.bodySmall
            )
            Spacer(Modifier.height(30.dp))

            GrowthCard(
                uiState = uiState,
                onDonationHistoryClick = onDonationHistoryClick,
                onDonationStatusClick = onDonationStatusClick,
                onDonationRankingClick = onDonationRankingClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GrowthIconRow(
    onDonationHistoryClick: () -> Unit,
    onDonationStatusClick: () -> Unit,
    onDonationRankingClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(Modifier.width(6.dp))
        GrowthIconItem(
            iconRes = R.drawable.donation_history_icon,
            label = "기부 기록",
            onClick = onDonationHistoryClick
        )
        Spacer(Modifier.width(6.dp))
        GrowthIconItem(
            iconRes = R.drawable.donation_status_icon,
            label = "현황",
            onClick = onDonationStatusClick
        )
        Spacer(Modifier.width(6.dp))
        GrowthIconItem(
            iconRes = R.drawable.donation_ranking,
            label = "랭킹",
            onClick = onDonationRankingClick
        )
    }
}

@Composable
private fun GrowthIconItem(
    iconRes: Int,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(50.dp)
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GrowthCard(
    uiState: GrowthUiState,
    onDonationHistoryClick: () -> Unit,
    onDonationStatusClick: () -> Unit,
    onDonationRankingClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TiggleSkyBlue),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GrowthIconRow(
                onDonationHistoryClick = onDonationHistoryClick,
                onDonationStatusClick = onDonationStatusClick,
                onDonationRankingClick = onDonationRankingClick
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .heightIn(min = 240.dp)
                    .background(Color.Transparent)
            ) {
                // 1) 캐릭터
                Character3D(level = uiState.growth.level, modifier = Modifier.fillMaxSize())

                // 2) 중앙 오버레이 Lottie (방법2: 진행도 직접 애니메이션)
                val LOTTIE_SIZE_DP = 260.dp           // ← 더 크게
                val LOTTIE_DURATION_MS = 3000         // ← 원하는 재생 시간(ms) 여기서 조절
                val SLOW_PORTION = 0.85f
                val MID_PROGRESS = 0.60f
                var playLottie by remember { mutableStateOf(false) }
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.heart2) // res/raw/heart.json
                )

                // 진행도 0f → 1f 를 내가 정한 시간에 맞춰 애니메이션
                val lottieProgress = remember { Animatable(0f) }

                LaunchedEffect(playLottie, composition) {
                    if (playLottie && composition != null) {
                        lottieProgress.snapTo(0f)
                        val t1 = (LOTTIE_DURATION_MS * SLOW_PORTION).toInt()
                        lottieProgress.animateTo(
                            targetValue = MID_PROGRESS,
                            animationSpec = tween(durationMillis = t1, easing = LinearEasing)
                        )

                        // 2) 뒤 구간: 남은 시간에 MID_PROGRESS → 1.0 (막판에 몰아서 빨라짐)
                        val t2 = LOTTIE_DURATION_MS - t1
                        lottieProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = t2,
                                easing = FastOutLinearInEasing
                            )
                        )
                        playLottie = false // 1회 재생 후 종료
                    }
                }

                if (playLottie && composition != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(LOTTIE_SIZE_DP) // ← 크기 키움
                    ) {
                        LottieAnimation(
                            composition = composition,
                            progress = { lottieProgress.value }
                        )
                    }
                }

                // 3) 드래그 하트 (중앙 드롭 성공 시 Lottie 트리거 + 하트 원위치 복귀)
                DraggableHeartDropTrigger(
                    iconRes = R.drawable.heart,
                    iconSize = 50.dp,
                    triggerRadius = 80.dp,
                    startOffsetBottomPadding = 16.dp,
                    onDropInCenter = { playLottie = true }
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "레벨 ${uiState.growth.level + 1}",
                    fontSize = 14.sp,
                    color = TiggleGrayText,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "쏠",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TiggleBlue
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "총 티끌: ${Formatter.formatCurrency(uiState.growth.totalAmount)}",
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )

            Spacer(Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { 0.7f }, // TODO: 실제 진행률
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = TiggleBlue,
                trackColor = Color.White.copy(alpha = 0.3f)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "다음 레벨까지 ${Formatter.formatCurrency(uiState.growth.toNextLevel.toLong())}",
                fontSize = 12.sp,
                color = TiggleGrayText
            )
        }
    }
}

/**
 * 중앙 원(반경 triggerRadius) 안에 드롭 시 onDropInCenter 호출.
 * 드롭 후 하트는 시작 위치(하단 중앙)로 부드럽게 복귀.
 */
@Composable
private fun DraggableHeartDropTrigger(
    iconRes: Int,
    iconSize: Dp,
    triggerRadius: Dp,
    startOffsetBottomPadding: Dp = 16.dp,
    onDropInCenter: () -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    var parentW by remember { mutableStateOf(0f) }
    var parentH by remember { mutableStateOf(0f) }
    val iconSizePx = with(density) { iconSize.toPx() }
    val triggerRadiusPx = with(density) { triggerRadius.toPx() }
    val bottomPadPx = with(density) { startOffsetBottomPadding.toPx() }

    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var initialized by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { c ->
                parentW = c.size.width.toFloat()
                parentH = c.size.height.toFloat()
                if (!initialized && parentW > 0f && parentH > 0f) {
                    val sx = parentW / 2f - iconSizePx / 2f
                    val sy = parentH - iconSizePx - bottomPadPx
                    scope.launch {
                        offsetX.snapTo(sx)
                        offsetY.snapTo(sy)
                    }
                    initialized = true
                }
            }
    ) {
        if (initialized) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = "드래그 하트",
                modifier = Modifier
                    .size(iconSize)
                    .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
                    .pointerInput(parentW, parentH, iconSizePx) {
                        detectDragGestures(
                            onDrag = { change, drag ->
                                // change.consume() // 버전에 따라 경고 나면 생략해도 OK
                                val nx = (offsetX.value + drag.x)
                                    .coerceIn(0f, (parentW - iconSizePx).coerceAtLeast(0f))
                                val ny = (offsetY.value + drag.y)
                                    .coerceIn(0f, (parentH - iconSizePx).coerceAtLeast(0f))
                                scope.launch { offsetX.snapTo(nx) }
                                scope.launch { offsetY.snapTo(ny) }
                            },
                            onDragEnd = {
                                val centerX = parentW / 2f
                                val centerY = parentH / 2f
                                val heartCenterX = offsetX.value + iconSizePx / 2f
                                val heartCenterY = offsetY.value + iconSizePx / 2f
                                val dist = kotlin.math.hypot(
                                    heartCenterX - centerX, heartCenterY - centerY
                                )

                                if (dist <= triggerRadiusPx) onDropInCenter()

                                val sx = parentW / 2f - iconSizePx / 2f
                                val sy = parentH - iconSizePx - bottomPadPx
                                scope.launch { offsetX.animateTo(sx, tween(220)) }
                                scope.launch { offsetY.animateTo(sy, tween(220)) }
                            }
                        )
                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GrowthScreenPreview() {
    GrowthScreen()
}
