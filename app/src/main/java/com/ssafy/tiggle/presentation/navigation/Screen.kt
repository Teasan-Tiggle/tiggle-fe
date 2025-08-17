package com.ssafy.tiggle.presentation.navigation

/**
 * 앱의 화면들을 정의하는 sealed class
 */
sealed class Screen(val route: String) {
    object UserList : Screen("user_list")
    object UserDetail : Screen("user_detail/{userId}") {
        fun createRoute(userId: Long) = "user_detail/$userId"
    }
}