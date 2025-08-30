package com.ssafy.tiggle.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * Lottie 기반 로딩 인디케이터 (버전 호환: progress/contentDescription 미사용)
 *
 * 사용 예)
 * 1) 인라인:
 *    TiggleLoadingIndicator.InlineRaw(R.raw.loading_spinner, size = 32.dp, speed = 0.8f)
 *
 * 2) 전체 화면 오버레이:
 *    TiggleLoadingIndicator.FullscreenRaw(
 *        isVisible = uiState.isLoading,
 *        rawRes = R.raw.loading_spinner,
 *        size = 180.dp,
 *        speed = 1.0f
 *    )
 */
object TiggleLoadingIndicator {

    // -------- 인라인 (raw 리소스) --------
    @Composable
    fun InlineRaw(
        rawRes: Int,
        modifier: Modifier = Modifier,
        size: Dp = 42.dp,
        speed: Float = 1f,
        iterations: Int = LottieConstants.IterateForever
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                modifier = modifier.size(size),
                // 아래 파라미터들은 대부분의 버전에서 공통 지원
                iterations = iterations,
                isPlaying = true,
                speed = speed
            )
        }
    }

    // -------- 인라인 (assets 폴더) --------
    @Composable
    fun InlineAsset(
        assetName: String, // 예: "loading.json" (src/main/assets/)
        modifier: Modifier = Modifier,
        size: Dp = 42.dp,
        speed: Float = 1f,
        iterations: Int = LottieConstants.IterateForever
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset(assetName))
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                modifier = modifier.size(size),
                iterations = iterations,
                isPlaying = true,
                speed = speed
            )
        }
    }

    // -------- 인라인 (URL) --------
    @Composable
    fun InlineUrl(
        url: String,
        modifier: Modifier = Modifier,
        size: Dp = 42.dp,
        speed: Float = 1f,
        iterations: Int = LottieConstants.IterateForever
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.Url(url))
        if (composition != null) {
            LottieAnimation(
                composition = composition,
                modifier = modifier.size(size),
                iterations = iterations,
                isPlaying = true,
                speed = speed
            )
        }
    }

    // -------- 전체 화면 오버레이 (raw 리소스) --------
    @Composable
    fun FullscreenRaw(
        isVisible: Boolean,
        rawRes: Int,
        modifier: Modifier = Modifier,
        size: Dp = 160.dp,
        speed: Float = 1f,
        backgroundColor: Color = Color.Black.copy(alpha = 0.35f)
    ) {
        if (!isVisible) return

        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(rawRes))
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundColor)
                // 터치 차단을 위해 클릭 이벤트 소비
                .clickable(onClick = {})
        ) {
            if (composition != null) {
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(size),
                    iterations = LottieConstants.IterateForever,
                    isPlaying = true,
                    speed = speed
                )
            }
        }
    }

    // -------- 전체 화면 오버레이 (URL) --------
    @Composable
    fun FullscreenUrl(
        isVisible: Boolean,
        url: String,
        modifier: Modifier = Modifier,
        size: Dp = 160.dp,
        speed: Float = 1f,
        backgroundColor: Color = Color.Black.copy(alpha = 0.35f)
    ) {
        if (!isVisible) return

        val composition by rememberLottieComposition(LottieCompositionSpec.Url(url))
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundColor)
                .clickable(onClick = {})
        ) {
            if (composition != null) {
                LottieAnimation(
                    composition = composition,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(size),
                    iterations = LottieConstants.IterateForever,
                    isPlaying = true,
                    speed = speed
                )
            }
        }
    }
}
