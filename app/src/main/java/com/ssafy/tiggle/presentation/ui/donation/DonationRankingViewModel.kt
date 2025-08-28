package com.ssafy.tiggle.presentation.ui.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.usecase.donation.GetDepartmentRankingUseCase
import com.ssafy.tiggle.domain.usecase.donation.GetUniversityRankingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DonationRankingViewModel @Inject constructor(
    private val getUniversityRankingUseCase: GetUniversityRankingUseCase,
    private val getDepartmentRankingUseCase: GetDepartmentRankingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonationRankingUiState())
    val uiState: StateFlow<DonationRankingUiState> = _uiState.asStateFlow()

    init {
        loadUniversityRanking()
    }

    fun onTabSelected(tab: RankingTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        
        when (tab) {
            RankingTab.UNIVERSITY -> {
                if (_uiState.value.universityRanking.isEmpty()) {
                    loadUniversityRanking()
                }
            }
            RankingTab.DEPARTMENT -> {
                if (_uiState.value.departmentRanking.isEmpty()) {
                    loadDepartmentRanking()
                }
            }
        }
    }

    private fun loadUniversityRanking() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getUniversityRankingUseCase().fold(
                onSuccess = { ranking ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            universityRanking = ranking
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "대학교 랭킹을 불러오는데 실패했습니다."
                        )
                    }
                }
            )
        }
    }

    private fun loadDepartmentRanking() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            getDepartmentRankingUseCase().fold(
                onSuccess = { ranking ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            departmentRanking = ranking
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "학과 랭킹을 불러오는데 실패했습니다."
                        )
                    }
                }
            )
        }
    }

    fun retry() {
        when (_uiState.value.selectedTab) {
            RankingTab.UNIVERSITY -> loadUniversityRanking()
            RankingTab.DEPARTMENT -> loadDepartmentRanking()
        }
    }
}
