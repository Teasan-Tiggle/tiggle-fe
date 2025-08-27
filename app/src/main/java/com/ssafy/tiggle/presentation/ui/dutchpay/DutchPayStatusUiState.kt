package com.ssafy.tiggle.presentation.ui.dutchpay

import com.ssafy.tiggle.domain.entity.dutchpay.DutchPaySummary

data class DutchPayStatusUiState(
    val isLoading: Boolean = false,
    val summary: DutchPaySummary? = null,
    val error: String? = null
)
