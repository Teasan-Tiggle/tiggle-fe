package com.ssafy.tiggle.presentation.ui.dutchpay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayDetail
import com.ssafy.tiggle.domain.repository.DutchPayRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DutchPayDetailViewModel @Inject constructor(
    private val dutchPayRepository: DutchPayRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DutchPayDetailUiState())
    val uiState: StateFlow<DutchPayDetailUiState> = _uiState.asStateFlow()

    fun loadDutchPayDetail(dutchPayId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            dutchPayRepository.getDutchPayDetail(dutchPayId)
                .onSuccess { detail ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        dutchPayDetail = detail,
                        error = null
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "더치페이 상세 정보를 불러오는데 실패했습니다."
                    )
                }
        }
    }
}

data class DutchPayDetailUiState(
    val isLoading: Boolean = false,
    val dutchPayDetail: DutchPayDetail? = null,
    val error: String? = null
)
