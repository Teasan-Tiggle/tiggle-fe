package com.ssafy.tiggle.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ssafy.tiggle.presentation.ui.user.UserScreen

/**
 * 앱의 네비게이션 그래프
 * 화면 간 이동을 관리
 */
@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.UserList.route,
        modifier = modifier
    ) {
        composable(Screen.UserList.route) {
            UserScreen()
        }

        // 추가 화면들은 여기에 정의
        // composable(Screen.UserDetail.route) { ... }
    }
}


