package com.ssafy.tiggle.presentation.ui.donation

import com.ssafy.tiggle.domain.entity.donation.DonationAccount
import com.ssafy.tiggle.domain.entity.donation.DonationCategory

data class DonationModalUiState(
    val isLoading: Boolean = false,
    val account: DonationAccount? = null,
    val selectedCategory: DonationCategory = DonationCategory.PLANET,
    val amount: String = "",
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)
