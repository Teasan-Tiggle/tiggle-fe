package com.ssafy.tiggle.presentation.ui.dutchpay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.usecase.dutchpay.GetDutchPayRequestDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DutchPayRequestDetailViewModel @Inject constructor(
    private val getDutchPayRequestDetailUseCase: GetDutchPayRequestDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DutchPayRequestDetailUiState())
    val uiState: StateFlow<DutchPayRequestDetailUiState> = _uiState.asStateFlow()

    fun loadDutchPayDetail(dutchPayId: Long) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch {
            getDutchPayRequestDetailUseCase(dutchPayId)
                .onSuccess { detail ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            dutchPayDetail = detail,
                            errorMessage = null
                        )
                    }
                }
                .onFailure { exception ->
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "더치페이 정보를 불러오는데 실패했습니다."
                        )
                    }
                }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
