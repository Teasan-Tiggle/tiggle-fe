package com.ssafy.tiggle.presentation.ui.piggybank

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

// PiggyBankDetailsRoute.kt
@Composable
fun PiggyBankDetailRoute(
    onBackClick: () -> Unit,
    viewModel: PiggyBankViewModel = hiltViewModel(),
    initialTab: PiggyTab? = PiggyTab.SpareChange,
    onMore: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        // 최초 진입 시 한 번만 로드
        viewModel.loadAllPiggyEntries(size = 20)
        initialTab?.let { viewModel.setSelectedTab(it) } // 뷰모델에 이 함수만 추가해주면 됨
    }

    PiggyBankDetailsScreen(
        uiState = state,
        onBack = onBackClick,
        onTabChange = { tab ->
            viewModel.setSelectedTab(tab)
            when (tab) {
                PiggyTab.SpareChange -> viewModel.reloadEntriesByType("TIGGLE")
                PiggyTab.DutchPay -> viewModel.reloadEntriesByType("DUTCHPAY")
            }
        },
        onMore = onMore
    )
}
