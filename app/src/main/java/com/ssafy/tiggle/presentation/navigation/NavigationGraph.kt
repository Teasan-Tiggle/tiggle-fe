package com.ssafy.tiggle.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.ssafy.tiggle.presentation.ui.auth.login.LoginScreen
import com.ssafy.tiggle.presentation.ui.auth.signup.SignUpScreen

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
        startDestination = Root.Main.route,
        modifier = modifier
    ) {
        //auth 그래프
        navigation(startDestination = AuthScreen.Login.route, route = Root.Auth.route) {
            composable(AuthScreen.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Root.Main.route) {
                            popUpTo(Root.Auth.route) { inclusive = true } //auth 스택 제거
                        }
                    },
                    onSignUpClick = {
                        // 회원가입 화면으로 이동
                        navController.navigate(AuthScreen.SignUp.route)
                    }
                )
            }

            composable(AuthScreen.SignUp.route) {
                // 회원가입 화면 컴포저블을 여기에 추가
                SignUpScreen(
                    onSignUpComplete = {
                        // 회원가입 성공 후 로그인 화면으로 이동
                        navController.navigate(AuthScreen.Login.route) {
                            popUpTo(AuthScreen.Login.route) { inclusive = true }
                        }
                    },
                    onBackClick = {
                        // 뒤로가기 버튼 클릭 시 이전 화면으로 이동
                        navController.popBackStack()
                    }
                )
            }
        }
        composable(Root.Main.route) {
            MainScaffold()
        }
    }
}


