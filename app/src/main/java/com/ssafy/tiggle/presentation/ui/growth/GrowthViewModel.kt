package com.ssafy.tiggle.presentation.ui.growth

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
            val result = growthUseCases.clickHeartUseCase()
            result
                .onSuccess { heartResult ->
                    _uiState.update {
                        it.copy(
                            heart = heartResult,
                            growth = it.growth.copy(
                                level = heartResult.level,
                                toNextLevel = heartResult.toNextLevel,
                                heart = heartResult.heart
                            )
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(errorMessage = e.message ?: "하트 사용 실패")
                    }
                }
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
