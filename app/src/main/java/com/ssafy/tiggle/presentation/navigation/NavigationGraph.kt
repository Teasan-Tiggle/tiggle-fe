package com.ssafy.tiggle.presentation.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.ssafy.tiggle.presentation.ui.dutchpay.DutchpayRecieveScreen
import com.ssafy.tiggle.presentation.ui.piggybank.OpenAccountScreen
import com.ssafy.tiggle.presentation.ui.piggybank.PiggyBankScreen
import com.ssafy.tiggle.presentation.ui.piggybank.RegisterAccountScreen

/**
 * 앱의 메인 네비게이션
 */
@Composable
fun NavigationGraph(
    intent: Intent?, // Nullable Intent를 받음
    onDeepLinkHandled: () -> Unit // 딥링크 처리 완료 콜백 함수
) {
    val startDestination = Screen.Login
    val navBackStack = rememberNavBackStack(startDestination)

    LaunchedEffect(intent) {
        val data: Uri? = intent?.data
        android.util.Log.d("NavigationGraph", "LaunchedEffect triggered - intent: $intent, data: $data")
        
        if (data != null && data.scheme == "tiggle" && data.host == "dutchpay") {
            android.util.Log.d("NavigationGraph", "Deep link detected: $data")
            val dutchPayId = data.lastPathSegment
            android.util.Log.d("NavigationGraph", "Extracted dutchPayId: $dutchPayId")
            
            if (dutchPayId != null) {
                try {
                    navBackStack.clear()
                    navBackStack.add(BottomScreen.PiggyBank)
                    navBackStack.add(Screen.DutchpayRecieve(dutchPayId.toLong()))
                    android.util.Log.d("NavigationGraph", "Navigation successful to DutchpayRecieve($dutchPayId)")
                } catch (e: Exception) {
                    android.util.Log.e("NavigationGraph", "Error navigating to DutchpayRecieve", e)
                }
            }
        }
        onDeepLinkHandled()
    }



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
                                navBackStack.add(Screen.OpenAccount)
                            },
                            onRegisterAccountClick = {
                                navBackStack.add(Screen.RegisterAccount)
                            },
                            onStartDutchPayClick = {
                                navBackStack.add(Screen.CreateDutchPay)
                            },
                            onBackClick = {
                                navBackStack.removeLastOrNull()
                            }
                        )
                    }

                    is Screen.OpenAccount -> NavEntry(key) {
                        OpenAccountScreen(
                            onBackClick = { navBackStack.removeLastOrNull() },
                            onFinish = {
                                navBackStack.removeLastOrNull()
                            }
                        )
                    }

                    is Screen.RegisterAccount -> NavEntry(key) {
                        RegisterAccountScreen(
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

                    is Screen.DutchpayRecieve -> NavEntry(key) {
                        // key에서 dutchPayId를 직접 꺼내서 화면에 전달합니다.
                        DutchpayRecieveScreen(dutchPayId = key.dutchPayId)
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


