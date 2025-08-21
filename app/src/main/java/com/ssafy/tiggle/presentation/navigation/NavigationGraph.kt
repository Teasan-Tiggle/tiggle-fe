package com.ssafy.tiggle.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.ssafy.tiggle.presentation.ui.auth.login.LoginScreen
import com.ssafy.tiggle.presentation.ui.auth.signup.SignUpScreen

import com.ssafy.tiggle.presentation.ui.piggybank.PiggyBankScreen

/**
 * 앱의 메인 네비게이션
 */
@Composable
fun NavigationGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route,
        modifier = Modifier.fillMaxSize()
    ) {
        // 인증 플로우
        navigation(startDestination = Screen.Login.route, route = Screen.Auth.route) {
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    },
                    onSignUpClick = {
                        navController.navigate(Screen.SignUp.route)
                    }
                )
            }

            composable(Screen.SignUp.route) {
                SignUpScreen(
                    onSignUpComplete = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // 메인 앱 (바텀 네비게이션)
        composable(Screen.Main.route) {
            MainBottomNavigation()
        }
    }
}

/**
 * 메인 화면의 바텀 네비게이션
 */
@Composable
private fun MainBottomNavigation() {
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigation(bottomNavController) }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.PiggyBank.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.PiggyBank.route) {
                PiggyBankScreen()
            }
            composable(Screen.Growth.route) {
                GrowthScreen()
            }
            composable(Screen.Shorts.route) {
                ShortsScreen()
            }
        }
    }
}

// 임시 화면들
@Composable
private fun GrowthScreen() {
    Text("성장")
}

@Composable
private fun ShortsScreen() {
    Text("숏폼")
}


