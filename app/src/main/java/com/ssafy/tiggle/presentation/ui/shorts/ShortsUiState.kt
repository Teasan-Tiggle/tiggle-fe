package com.ssafy.tiggle.presentation.ui.shorts

data class ShortsUiState(
    val videos: List<ShortsVideo> = emptyList(),
    val currentVideoIndex: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val hasReachedEnd: Boolean = false
)
