package com.ssafy.tiggle.presentation.navigation

/**
 * 앱의 모든 화면을 관리하는 sealed class
 */
sealed class Screen(val route: String) {
    // 인증 관련
    object Auth : Screen("auth")
    object Login : Screen("login")
    object SignUp : Screen("signup")

    // 메인 앱
    object Main : Screen("main")

    // 바텀 네비게이션 화면들
    object PiggyBank : Screen("piggybank")
    object Growth : Screen("growth")
    object Shorts : Screen("shorts")
}
