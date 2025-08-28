package com.ssafy.tiggle.presentation.ui.donation

import com.ssafy.tiggle.domain.entity.donation.DonationRank

data class DonationRankingUiState(
    val isLoading: Boolean = false,
    val universityRanking: List<DonationRank> = emptyList(),
    val departmentRanking: List<DonationRank> = emptyList(),
    val selectedTab: RankingTab = RankingTab.UNIVERSITY,
    val error: String? = null
)

enum class RankingTab {
    UNIVERSITY,
    DEPARTMENT
}
