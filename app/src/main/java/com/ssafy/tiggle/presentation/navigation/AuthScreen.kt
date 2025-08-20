package com.ssafy.tiggle.presentation.navigation

/**
 * 앱의 화면들을 정의하는 sealed class
 */
sealed class AuthScreen(val route: String) {
    object Login : AuthScreen("login")
    object SignUp : AuthScreen("signup")
}