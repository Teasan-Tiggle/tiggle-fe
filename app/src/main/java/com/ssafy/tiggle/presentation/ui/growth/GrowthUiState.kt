package com.ssafy.tiggle.presentation.ui.growth

/**
 * 성장 화면의 UI 상태
 */
data class GrowthUiState(
    val isLoading: Boolean = false,
    val totalDonationAmount: Int = 17800, // 총 티끌 금액
    val nextGoalAmount: Int = 2500, // 다음 레벨까지 필요한 금액
    val currentLevel: String = "쓸", // 현재 레벨
    val characterStatus: String = "행복", // 캐릭터 상태
    val donationHistory: List<DonationRecord> = emptyList(),
    val donationRanking: List<RankingItem> = emptyList(),
    val errorMessage: String? = null
)

/**
 * 기부 기록 아이템
 */
data class DonationRecord(
    val id: String,
    val amount: Int,
    val date: String,
    val description: String
)

/**
 * 랭킹 아이템
 */
data class RankingItem(
    val rank: Int,
    val name: String,
    val amount: Int,
    val isCurrentUser: Boolean = false
)
