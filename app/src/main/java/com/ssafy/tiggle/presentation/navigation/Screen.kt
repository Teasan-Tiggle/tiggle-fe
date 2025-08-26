package com.ssafy.tiggle.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

/**
 * 앱의 모든 화면을 관리하는 sealed class
 */

sealed interface BottomScreen : NavKey {
    // 바텀 네비게이션 화면들
    @Serializable
    object PiggyBank : BottomScreen

    @Serializable
    object Growth : BottomScreen

    @Serializable
    object Shorts : BottomScreen
}

sealed interface Screen : NavKey {
    // 인증 관련
    @Serializable
    object Login : Screen

    @Serializable
    object SignUp : Screen

    @Serializable
    object OpenAccount : Screen

    @Serializable
    object RegisterAccount : Screen

    @Serializable
    object CreateDutchPay : Screen

    @Serializable
    object DonationHistory : Screen
}
