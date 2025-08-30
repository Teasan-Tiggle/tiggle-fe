package com.ssafy.tiggle.presentation.ui.growth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.usecase.growth.GrowthUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class GrowthViewModel @Inject constructor(
    val growthUseCases: GrowthUseCases
) : ViewModel() {

    private val _uiState = MutableStateFlow(GrowthUiState())
    val uiState: StateFlow<GrowthUiState> = _uiState.asStateFlow()

    init {
        // 초기 데이터 로드 (나중에 실제 API 연결)
        loadGrowthData()
    }

    private fun loadGrowthData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = growthUseCases.getGrowthResultUseCase()

            result
                .onSuccess { growth ->
                    Log.d("GrowthViewModel", "✅ 성장 데이터 로드 성공: $growth")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            growth = growth,
                            characterStatus = "행복"
                        )
                    }
                }
                .onFailure { e ->
                    val isNotFound = (e is HttpException && e.code() == 404)
                    Log.e("GrowthViewModel", "❌ 성장 데이터 로드 실패: ${e.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "성장 조회에 실패했습니다."
                        )
                    }
                }
        }
    }

    fun useHeart() {
        viewModelScope.launch {
            Log.d("GrowthViewModel", "💖 하트 사용 시작")
            val result = growthUseCases.clickHeartUseCase()
            result
                .onSuccess { heartResult ->
                    // 이전 레벨 저장
                    val previousLevel = _uiState.value.growth.level
                    Log.d("GrowthViewModel", "✅ 하트 사용 성공: $heartResult, 이전 레벨: $previousLevel")
                    
                    // 상태를 한 번에 업데이트하여 화면 갱신 보장
                    _uiState.update { currentState ->
                        currentState.copy(
                            heart = heartResult,
                            growth = currentState.growth.copy(
                                experiencePoints = heartResult.experiencePoints,
                                level = heartResult.level,
                                toNextLevel = heartResult.toNextLevel,
                                heart = heartResult.heart
                            ),
                            // 에러 메시지 초기화
                            errorMessage = null
                        )
                    }
                    
                    // 레벨업 체크 및 처리
                    if (heartResult.level > previousLevel) {
                        Log.d("GrowthViewModel", "🎉 레벨업 발생: $previousLevel → ${heartResult.level}")
                        handleLevelUp(previousLevel, heartResult.level)
                    }
                }
                .onFailure { e ->
                    Log.e("GrowthViewModel", "❌ 하트 사용 실패: ${e.message}")
                    _uiState.update {
                        it.copy(errorMessage = e.message ?: "하트 사용 실패")
                    }
                }
        }
    }
    
    /**
     * 레벨업 처리 로직
     */
    private fun handleLevelUp(previousLevel: Int, newLevel: Int) {
        Log.d("GrowthViewModel", "🎊 레벨업 처리: $previousLevel → $newLevel")
        _uiState.update { currentState ->
            currentState.copy(
                isLevelUp = true,
                previousLevel = previousLevel
            )
        }
        
        // 3초 후 레벨업 상태 초기화
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            _uiState.update { it.copy(isLevelUp = false) }
            Log.d("GrowthViewModel", "⏰ 레벨업 애니메이션 종료")
        }
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
