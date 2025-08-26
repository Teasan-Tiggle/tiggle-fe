package com.ssafy.tiggle.presentation.ui.shorts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShortsViewModel @Inject constructor(
    // private val shortsRepository: ShortsRepository // 나중에 구현
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShortsUiState())
    val uiState: StateFlow<ShortsUiState> = _uiState.asStateFlow()

    init {
        loadInitialVideos()
    }

    private fun loadInitialVideos() {
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                // TODO: 실제 비디오 데이터는 Repository에서 가져오기
                val mockVideos = generateMockVideos()
                _uiState.update { 
                    it.copy(
                        videos = mockVideos,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    fun setCurrentVideoIndex(index: Int) {
        _uiState.update { it.copy(currentVideoIndex = index) }
        
        // 마지막에 가까워지면 더 많은 비디오 로드
        if (index >= _uiState.value.videos.size - 2 && !_uiState.value.hasReachedEnd) {
            loadMoreVideos()
        }
    }

    fun loadMoreVideos() {
        if (_uiState.value.isLoading) return
        
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch {
            try {
                // TODO: 더 많은 비디오 로드 로직
                val moreVideos = generateMockVideos(startIndex = _uiState.value.videos.size)
                _uiState.update { currentState ->
                    currentState.copy(
                        videos = currentState.videos + moreVideos,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message
                    )
                }
            }
        }
    }

    fun toggleLike(videoId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                videos = currentState.videos.map { video ->
                    if (video.id == videoId) {
                        video.copy(
                            isLiked = !video.isLiked,
                            likeCount = if (video.isLiked) video.likeCount - 1 else video.likeCount + 1
                        )
                    } else {
                        video
                    }
                }
            )
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    // Mock 데이터 생성 (나중에 제거)
    private fun generateMockVideos(startIndex: Int = 0): List<ShortsVideo> {
        val mockVideos = mutableListOf<ShortsVideo>()
        
        // 테스트용 샘플 비디오 URL들 - 안정적인 Google 스토리지 사용
        // 실제 프로젝트에서는 로컬 파일이나 자체 서버 URL 사용 권장
        val sampleVideoUrls = listOf(
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
        )
        
        for (i in startIndex until startIndex + 10) {
            mockVideos.add(
                ShortsVideo(
                    id = "video_$i",
                    videoUrl = sampleVideoUrls[i % sampleVideoUrls.size],
                    title = "재미있는 숏폼 영상 #$i\n금융 꿀팁과 일상 브이로그",
                    username = "티끌유저${i + 1}",
                    likeCount = (100..10000).random(),
                    shareCount = (10..1000).random(),
                    viewCount = (1000..100000).random(),
                    hashtags = listOf("금융", "절약", "투자", "일상", "브이로그", "꿀팁").shuffled().take(3),
                    isLiked = false
                )
            )
        }
        
        return mockVideos
    }
}
