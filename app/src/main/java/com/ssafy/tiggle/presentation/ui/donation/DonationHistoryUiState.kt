package com.ssafy.tiggle.presentation.ui.donation

import com.ssafy.tiggle.domain.entity.donation.DonationHistory

data class DonationHistoryUiState(
    val isLoading: Boolean = false,
    val donationHistoryList: List<DonationHistory> = emptyList(),
    val errorMessage: String? = null
)
