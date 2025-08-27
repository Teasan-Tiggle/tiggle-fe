package com.ssafy.tiggle.presentation.ui.dutchpay

import com.ssafy.tiggle.domain.entity.dutchpay.DutchPaySummary
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayItem

data class DutchPayStatusUiState(
    val isLoading: Boolean = false,
    val summary: DutchPaySummary? = null,
    val inProgressItems: List<DutchPayItem> = emptyList(),
    val completedItems: List<DutchPayItem> = emptyList(),
    val inProgressCursor: String? = null,
    val completedCursor: String? = null,
    val hasNextInProgress: Boolean = false,
    val hasNextCompleted: Boolean = false,
    val selectedTabIndex: Int = 0,
    val error: String? = null
)
