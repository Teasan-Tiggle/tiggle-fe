package com.ssafy.tiggle.presentation.ui.dutchpay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.usecase.dutchpay.GetDutchPaySummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DutchPayStatusViewModel @Inject constructor(
    private val getDutchPaySummaryUseCase: GetDutchPaySummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DutchPayStatusUiState())
    val uiState: StateFlow<DutchPayStatusUiState> = _uiState.asStateFlow()

    init {
        loadDutchPaySummary()
    }

    fun loadDutchPaySummary() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            getDutchPaySummaryUseCase().fold(
                onSuccess = { summary ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        summary = summary
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "더치페이 현황을 불러오는데 실패했습니다."
                    )
                }
            )
        }
    }
}
