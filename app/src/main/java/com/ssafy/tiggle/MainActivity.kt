package com.ssafy.tiggle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.ssafy.tiggle.presentation.navigation.NavigationGraph
import com.ssafy.tiggle.presentation.ui.theme.TiggleTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * 메인 액티비티
 * Hilt를 사용하여 의존성 주입을 받습니다
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TiggleTheme {
                NavigationGraph()
            }
        }
    }
}