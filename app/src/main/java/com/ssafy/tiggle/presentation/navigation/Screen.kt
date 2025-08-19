package com.ssafy.tiggle.presentation.navigation

/**
 * 앱의 화면들을 정의하는 sealed class
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object SignUp : Screen("signup")

}