package com.ssafy.tiggle.presentation.ui.dutchpay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.usecase.dutchpay.GetDutchPaySummaryUseCase
import com.ssafy.tiggle.domain.usecase.dutchpay.GetDutchPayListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DutchPayStatusViewModel @Inject constructor(
    private val getDutchPaySummaryUseCase: GetDutchPaySummaryUseCase,
    private val getDutchPayListUseCase: GetDutchPayListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DutchPayStatusUiState())
    val uiState: StateFlow<DutchPayStatusUiState> = _uiState.asStateFlow()

    init {
        loadDutchPaySummary()
        loadDutchPayList()
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

    fun loadDutchPayList(isRefresh: Boolean = true) {
        viewModelScope.launch {
            if (isRefresh) {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            }
            
            val currentState = _uiState.value
            val tab = if (currentState.selectedTabIndex == 0) "IN_PROGRESS" else "COMPLETED"
            val cursor = if (isRefresh) null else {
                if (currentState.selectedTabIndex == 0) currentState.inProgressCursor else currentState.completedCursor
            }
            
            getDutchPayListUseCase(tab, cursor).fold(
                onSuccess = { list ->
                    val newItems = if (isRefresh) list.items else {
                        if (currentState.selectedTabIndex == 0) {
                            currentState.inProgressItems + list.items
                        } else {
                            currentState.completedItems + list.items
                        }
                    }
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        inProgressItems = if (currentState.selectedTabIndex == 0) newItems else currentState.inProgressItems,
                        completedItems = if (currentState.selectedTabIndex == 1) newItems else currentState.completedItems,
                        inProgressCursor = if (currentState.selectedTabIndex == 0) list.nextCursor else currentState.inProgressCursor,
                        completedCursor = if (currentState.selectedTabIndex == 1) list.nextCursor else currentState.completedCursor,
                        hasNextInProgress = if (currentState.selectedTabIndex == 0) list.hasNext else currentState.hasNextInProgress,
                        hasNextCompleted = if (currentState.selectedTabIndex == 1) list.hasNext else currentState.hasNextCompleted
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "더치페이 내역을 불러오는데 실패했습니다."
                    )
                }
            )
        }
    }

    fun onTabSelected(tabIndex: Int) {
        if (_uiState.value.selectedTabIndex != tabIndex) {
            _uiState.value = _uiState.value.copy(selectedTabIndex = tabIndex)
            loadDutchPayList()
        }
    }

    fun loadMoreItems() {
        val currentState = _uiState.value
        val hasNext = if (currentState.selectedTabIndex == 0) currentState.hasNextInProgress else currentState.hasNextCompleted
        
        if (hasNext && !currentState.isLoading) {
            loadDutchPayList(isRefresh = false)
        }
    }
}
