package com.ssafy.tiggle.presentation.ui.growth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GrowthViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(GrowthUiState())
    val uiState: StateFlow<GrowthUiState> = _uiState.asStateFlow()
    
    init {
        // 초기 데이터 로드 (나중에 실제 API 연결)
        loadGrowthData()
    }
    
    private fun loadGrowthData() {
        // TODO: 실제 API에서 데이터 로드
        // 현재는 더미 데이터 사용
        _uiState.value = _uiState.value.copy(
            totalDonationAmount = 17800,
            nextGoalAmount = 2500,
            currentLevel = "쓸",
            characterStatus = "행복"
        )
    }
    
    fun onDonationHistoryClick() {
        // TODO: 기부 기록 화면으로 이동
    }
    
    fun onDonationStatusClick() {
        // TODO: 기부 현황 화면으로 이동
    }
    
    fun onDonationRankingClick() {
        // TODO: 기부 랭킹 화면으로 이동
    }
}
