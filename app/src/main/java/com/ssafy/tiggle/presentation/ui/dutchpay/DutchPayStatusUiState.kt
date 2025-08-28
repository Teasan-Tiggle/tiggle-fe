package com.ssafy.tiggle.presentation.ui.dutchpay

import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayItem
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPaySummary

data class DutchPayStatusUiState(
    val isLoading: Boolean = true, // 초기 전체 화면 로딩
    val isLoadingMore: Boolean = false, // 리스트 하단 더보기 로딩
    val error: String? = null,
    val summary: DutchPaySummary? = null,
    val selectedTabIndex: Int = 0,
    val inProgressItems: List<DutchPayItem> = emptyList(),
    val completedItems: List<DutchPayItem> = emptyList(),
    val inProgressCursor: String? = null,
    val completedCursor: String? = null,
    val hasNextInProgress: Boolean = false,
    val hasNextCompleted: Boolean = false
)