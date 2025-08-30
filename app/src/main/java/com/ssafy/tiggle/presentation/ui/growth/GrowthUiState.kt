package com.ssafy.tiggle.presentation.ui.growth

import com.ssafy.tiggle.domain.entity.growth.GrowthResult
import com.ssafy.tiggle.domain.entity.growth.HeartResult

/**
 * 성장 화면의 UI 상태
 */
data class GrowthUiState(
    val isLoading: Boolean = false,
    val growth: GrowthResult = GrowthResult(),
    val characterStatus: String = "행복", // 캐릭터 상태
    val donationHistory: List<DonationRecord> = emptyList(),
    val donationRanking: List<RankingItem> = emptyList(),
    val heart: HeartResult = HeartResult(),
    val errorMessage: String? = null,
    val isLevelUp: Boolean = false, // 레벨업 상태
    val previousLevel: Int = 0 // 이전 레벨
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
