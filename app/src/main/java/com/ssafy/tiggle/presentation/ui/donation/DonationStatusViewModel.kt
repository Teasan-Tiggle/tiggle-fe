package com.ssafy.tiggle.presentation.ui.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.entity.donation.DonationStatusType
import com.ssafy.tiggle.domain.usecase.donation.GetDonationStatusUseCase
import com.ssafy.tiggle.domain.usecase.donation.GetDonationSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DonationStatusViewModel @Inject constructor(
    private val getDonationSummaryUseCase: GetDonationSummaryUseCase,
    private val getDonationStatusUseCase: GetDonationStatusUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonationStatusUiState())
    val uiState: StateFlow<DonationStatusUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        loadDonationSummary()
        loadDonationStatus(_uiState.value.currentStatusType)
    }

    fun loadDonationSummary() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // 기부 요약 정보 로드
                val summaryResult = getDonationSummaryUseCase()
                if (summaryResult.isSuccess) {
                    _uiState.value = _uiState.value.copy(donationSummary = summaryResult.getOrNull())
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "기부 요약 정보를 불러오는 중 오류가 발생했습니다."
                    )
                    return@launch
                }
                
                // 기부 현황 로드 (초기값: 전체 학교)
                loadDonationStatus(_uiState.value.currentStatusType)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "데이터를 불러오는 중 오류가 발생했습니다."
                )
            }
        }
    }

    fun onStatusTypeChanged(type: DonationStatusType) {
        if (_uiState.value.currentStatusType != type) {
            _uiState.value = _uiState.value.copy(currentStatusType = type)
            loadDonationStatus(type)
        }
    }

    fun loadDonationStatus(type: DonationStatusType) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            val statusResult = getDonationStatusUseCase(type)
            if (statusResult.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    donationStatus = statusResult.getOrNull()
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "기부 현황을 불러오는 중 오류가 발생했습니다."
                )
            }
        }
    }
}
