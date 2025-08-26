package com.ssafy.tiggle.presentation.ui.donation

import com.ssafy.tiggle.domain.entity.donation.DonationStatus
import com.ssafy.tiggle.domain.entity.donation.DonationStatusType
import com.ssafy.tiggle.domain.entity.donation.DonationSummary

data class DonationStatusUiState(
    val isLoading: Boolean = false,
    val donationSummary: DonationSummary? = null,
    val donationStatus: DonationStatus? = null,
    val currentStatusType: DonationStatusType = DonationStatusType.ALL_UNIVERSITY,
    val errorMessage: String? = null
)
