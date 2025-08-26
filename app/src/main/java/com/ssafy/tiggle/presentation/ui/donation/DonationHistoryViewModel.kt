package com.ssafy.tiggle.presentation.ui.donation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.usecase.donation.GetDonationHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DonationHistoryViewModel @Inject constructor(
    private val getDonationHistoryUseCase: GetDonationHistoryUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DonationHistoryUiState())
    val uiState: StateFlow<DonationHistoryUiState> = _uiState.asStateFlow()
    
    fun loadDonationHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            getDonationHistoryUseCase()
                .onSuccess { historyList ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        donationHistoryList = historyList
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "알 수 없는 오류가 발생했습니다."
                    )
                }
        }
    }
}
