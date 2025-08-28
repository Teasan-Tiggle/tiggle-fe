package com.ssafy.tiggle.presentation.ui.dutchpay

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.tiggle.domain.usecase.dutchpay.GetDutchPaySummaryUseCase
import com.ssafy.tiggle.domain.usecase.dutchpay.GetDutchPayListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val summaryDeferred = async { getDutchPaySummaryUseCase() }
            val inProgressListDeferred = async { getDutchPayListUseCase(tab = "IN_PROGRESS", cursor = null) }
            val completedListDeferred = async { getDutchPayListUseCase(tab = "COMPLETED", cursor = null) }

            val summaryResult = summaryDeferred.await()
            val inProgressListResult = inProgressListDeferred.await()
            val completedListResult = completedListDeferred.await()

            _uiState.update { currentState ->
                var newState = currentState.copy(isLoading = false)
                summaryResult.onSuccess { summary ->
                    newState = newState.copy(summary = summary)
                }.onFailure { exception ->
                    newState = newState.copy(error = exception.message ?: "요약 정보 로딩 실패")
                }
                inProgressListResult.onSuccess { list ->
                    newState = newState.copy(
                        inProgressItems = list.items,
                        inProgressCursor = list.nextCursor,
                        hasNextInProgress = list.hasNext
                    )
                }.onFailure { exception ->
                    newState = newState.copy(error = exception.message ?: "진행중 내역 로딩 실패")
                }
                completedListResult.onSuccess { list ->
                    newState = newState.copy(
                        completedItems = list.items,
                        completedCursor = list.nextCursor,
                        hasNextCompleted = list.hasNext
                    )
                }.onFailure { exception ->
                    newState = newState.copy(error = exception.message ?: "완료 내역 로딩 실패")
                }
                newState
            }
        }
    }

    // 새로고침 또는 탭 선택 시 호출
    fun loadDutchPayList(isRefresh: Boolean = true) {
        viewModelScope.launch {
            // isRefresh는 새로고침, isRefresh가 아니면 더보기 로딩으로 간주
            _uiState.update {
                if (isRefresh) it.copy(isLoading = true, error = null)
                else it.copy(isLoadingMore = true, error = null)
            }

            val currentState = _uiState.value
            val tab = if (currentState.selectedTabIndex == 0) "IN_PROGRESS" else "COMPLETED"
            val cursor = if (isRefresh) null else {
                if (currentState.selectedTabIndex == 0) currentState.inProgressCursor else currentState.completedCursor
            }

            getDutchPayListUseCase(tab, cursor).fold(
                onSuccess = { list ->
                    _uiState.update { state ->
                        val newItems = if (isRefresh) list.items else {
                            if (state.selectedTabIndex == 0) state.inProgressItems + list.items
                            else state.completedItems + list.items
                        }

                        state.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            inProgressItems = if (state.selectedTabIndex == 0) newItems else state.inProgressItems,
                            completedItems = if (state.selectedTabIndex == 1) newItems else state.completedItems,
                            inProgressCursor = if (state.selectedTabIndex == 0) list.nextCursor else state.inProgressCursor,
                            completedCursor = if (state.selectedTabIndex == 1) list.nextCursor else state.completedCursor,
                            hasNextInProgress = if (state.selectedTabIndex == 0) list.hasNext else state.hasNextInProgress,
                            hasNextCompleted = if (state.selectedTabIndex == 1) list.hasNext else state.hasNextCompleted
                        )
                    }
                },
                onFailure = { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = exception.message ?: "더치페이 내역을 불러오는데 실패했습니다."
                        )
                    }
                }
            )
        }
    }

    fun onTabSelected(tabIndex: Int) {
        if (_uiState.value.selectedTabIndex != tabIndex) {
            _uiState.update { it.copy(selectedTabIndex = tabIndex) }
            // 탭을 선택하면 항상 새로고침
            loadDutchPayList(isRefresh = true)
        }
    }

    fun loadMoreItems() {
        val currentState = _uiState.value
        val hasNext = if (currentState.selectedTabIndex == 0) currentState.hasNextInProgress else currentState.hasNextCompleted

        // 초기 로딩 중이거나, 더보기 로딩 중이 아닐 때만 호출
        if (hasNext && !currentState.isLoading && !currentState.isLoadingMore) {
            loadDutchPayList(isRefresh = false)
        }
    }
}