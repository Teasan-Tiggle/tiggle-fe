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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
    
    // 상태 변화 감지를 위한 LaunchedEffect
    LaunchedEffect(uiState.growth.level, uiState.growth.heart, uiState.growth.experiencePoints) {
        // 상태가 변경될 때마다 로그 출력 (디버깅용)
        android.util.Log.d("GrowthScreen", "🔄 UI 상태 업데이트: 레벨=${uiState.growth.level}, 하트=${uiState.growth.heart}, 경험치=${uiState.growth.experiencePoints}")
    }

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
                modifier = Modifier.weight(1f),
                viewModel = viewModel
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
    modifier: Modifier = Modifier,
    viewModel: GrowthViewModel = hiltViewModel()
) {
    // 진행률 계산 - experiencePoints 기반으로 개선
    val progress = remember(
        uiState.growth.experiencePoints,
        uiState.growth.toNextLevel
    ) {
        val currentExp = uiState.growth.experiencePoints.toFloat()
        val totalExp = currentExp + uiState.growth.toNextLevel
        if (totalExp == 0f) 0f else currentExp / totalExp
    }

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
                // 캐릭터
                key(uiState.growth.level, uiState.growth.experiencePoints) {
                    Character3D(level = uiState.growth.level, modifier = Modifier.fillMaxSize())
                }
                
                // 레벨업 애니메이션
                if (uiState.isLevelUp) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxSize()
                    ) {
                        // 레벨업 축하 텍스트
                        Text(
                            text = "레벨업! 🎉",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TiggleBlue,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .background(
                                    Color.White.copy(alpha = 0.9f),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        )
                    }
                }

                // Lottie 애니메이션
                val LOTTIE_SIZE_DP = 260.dp
                val LOTTIE_DURATION_MS = 3000
                val SLOW_PORTION = 0.85f
                val MID_PROGRESS = 0.60f
                var playLottie by remember { mutableStateOf(false) }
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.heart2)
                )
                val lottieProgress = remember { Animatable(0f) }

                LaunchedEffect(playLottie, composition) {
                    if (playLottie && composition != null) {
                        lottieProgress.snapTo(0f)
                        val t1 = (LOTTIE_DURATION_MS * SLOW_PORTION).toInt()
                        lottieProgress.animateTo(
                            targetValue = MID_PROGRESS,
                            animationSpec = tween(durationMillis = t1, easing = LinearEasing)
                        )
                        val t2 = LOTTIE_DURATION_MS - t1
                        lottieProgress.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(
                                durationMillis = t2,
                                easing = FastOutLinearInEasing
                            )
                        )
                        playLottie = false
                    }
                }

                if (playLottie && composition != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(LOTTIE_SIZE_DP)
                    ) {
                        LottieAnimation(
                            composition = composition,
                            progress = { lottieProgress.value }
                        )
                    }
                }

                // 드래그 하트
                key(uiState.growth.heart) {
                    DraggableHeartDropTrigger(
                        iconRes = R.drawable.heart,
                        iconSize = 50.dp,
                        triggerRadius = 80.dp,
                        startOffsetBottomPadding = 16.dp,
                        enabled = uiState.growth.heart > 0, // 0개면 비활성화
                        onDropInCenter = {
                            playLottie = true
                            viewModel.useHeart() // 하트 사용 API 호출
                        }
                    )
                }
            }

            // 레벨 + 하트 개수 표시
            key(uiState.growth.level, uiState.growth.heart) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "레벨 ${uiState.growth.level}",
                            fontSize = 14.sp,
                            color = TiggleGrayText,
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.7f),
                                    RoundedCornerShape(12.dp)
                                )
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
                    Text(
                        text = "❤️ ${uiState.growth.heart}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.growth.heart > 0) Color.Red else TiggleGrayText
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            key(uiState.growth.experiencePoints, uiState.growth.toNextLevel) {
                Text(
                    text = "총 티끌: ${Formatter.formatCurrency(uiState.growth.totalAmount)}",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(Modifier.height(4.dp))
                
                Text(
                    text = "경험치: ${uiState.growth.experiencePoints}",
                    fontSize = 14.sp,
                    color = TiggleGrayText,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = { progress }, // 실제 진행률 적용
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = TiggleBlue,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "다음 레벨까지 ${uiState.growth.toNextLevel} 경험치",
                    fontSize = 12.sp,
                    color = TiggleGrayText
                )
            }
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
    onDropInCenter: () -> Unit,
    enabled: Boolean
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
                    .alpha(if (enabled) 1f else 0.3f)
                    .then(
                        if (enabled) Modifier.pointerInput(parentW, parentH, iconSizePx) {
                            detectDragGestures(
                                onDrag = { _, drag ->
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
                        } else Modifier
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GrowthScreenPreview() {
    GrowthScreen()
}
