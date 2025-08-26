package com.ssafy.tiggle.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.ssafy.tiggle.presentation.ui.auth.login.LoginScreen
import com.ssafy.tiggle.presentation.ui.auth.signup.SignUpScreen
import com.ssafy.tiggle.presentation.ui.dutchpay.CreateDutchPayScreen
import com.ssafy.tiggle.presentation.ui.piggybank.MainAccountDetailScreen
import com.ssafy.tiggle.presentation.ui.piggybank.OpenAccountMode
import com.ssafy.tiggle.presentation.ui.piggybank.OpenAccountScreen
import com.ssafy.tiggle.presentation.ui.piggybank.PiggyBankDetailRoute
import com.ssafy.tiggle.presentation.ui.piggybank.PiggyBankScreen
import com.ssafy.tiggle.presentation.ui.piggybank.RegisterAccountScreen

/**
 * 앱의 메인 네비게이션
 */
@Composable
fun NavigationGraph() {
    val startDestination = Screen.Login
    val navBackStack = rememberNavBackStack(startDestination)

    Scaffold(
        bottomBar = {
            if (navBackStack.last() is BottomScreen)
                BottomNavigation(navBackStack)
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = navBackStack,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            entryDecorators = listOf(
                rememberSceneSetupNavEntryDecorator(),
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = { key ->
                when (key) {
                    is Screen.Login -> NavEntry(key) {
                        LoginScreen(
                            onLoginSuccess = {
                                navBackStack.add(BottomScreen.PiggyBank)
                            },
                            onSignUpClick = {
                                navBackStack.add(Screen.SignUp)
                            }
                        )
                    }

                    is Screen.SignUp -> NavEntry(key) {
                        SignUpScreen(
                            onSignUpComplete = {
                                navBackStack.add(Screen.Login)
                            },
                            onBackClick = {
                                navBackStack.removeLastOrNull()
                            }
                        )
                    }

                    is BottomScreen.Growth -> NavEntry(key) {
                        GrowthScreen()
                    }

                    is BottomScreen.Shorts -> NavEntry(key) {
                        ShortsScreen()
                    }

                    is BottomScreen.PiggyBank -> NavEntry(key) {
                        PiggyBankScreen(
                            onOpenAccountClick = {
                                navBackStack.add(Screen.OpenAccount())
                            },
                            onRegisterAccountClick = {
                                navBackStack.add(Screen.RegisterAccount(isEdit = false))
                            },
                            onStartDutchPayClick = {
                                navBackStack.add(Screen.CreateDutchPay)
                            },

                            onAccountClick = { accountNo ->
                                navBackStack.add(Screen.MainAccountDetail(accountNo))
                            },
                            onShowPiggyBankDetailClick = {
                                navBackStack.add(Screen.PiggyBankDetail)
                            },
                            onEditLinkedAccountClick = {
                                navBackStack.add(Screen.RegisterAccount(isEdit = true))
                            }
                        )
                    }

                    is Screen.OpenAccount -> NavEntry(key) {
                        OpenAccountScreen(
                            mode = key.mode,
                            onBackClick = { navBackStack.removeLastOrNull() },
                            onFinish = {
                                navBackStack.removeLastOrNull()
                            },
                        )
                    }

                    is Screen.RegisterAccount -> NavEntry(key) {
                        RegisterAccountScreen(
                            isEdit = key.isEdit,
                            onBackClick = { navBackStack.removeLastOrNull() },
                            onFinish = {
                                navBackStack.removeLastOrNull()
                            }
                        )
                    }

                    is Screen.CreateDutchPay -> NavEntry(key) {
                        CreateDutchPayScreen(
                            onBackClick = { navBackStack.removeLastOrNull() },
                            onFinish = { navBackStack.removeLastOrNull() }
                        )
                    }

                    is Screen.MainAccountDetail -> NavEntry(key) {
                        MainAccountDetailScreen(
                            accountNo = key.accountNo,
                            onBackClick = { navBackStack.removeLastOrNull() }
                        )
                    }

                    is Screen.PiggyBankDetail -> NavEntry(key) {
                        PiggyBankDetailRoute(
                            onBackClick = { navBackStack.removeLastOrNull() },
                            onMore = { navBackStack.add(Screen.OpenAccount(OpenAccountMode.SIMPLE)) }
                        )
                    }

                    else -> throw IllegalArgumentException("Unknown route: $key")
                }

            }
        )
    }

}

/**
 * 메인 화면의 바텀 네비게이션
 */
// 임시 화면들
@Composable
private fun GrowthScreen() {
    Text("성장")
}

@Composable
private fun ShortsScreen() {
    Text("숏폼")
}


