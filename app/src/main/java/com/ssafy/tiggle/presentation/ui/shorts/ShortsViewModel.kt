package com.ssafy.tiggle.presentation.ui.shorts

import android.content.Context
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
                // 로컬 비디오 파일 사용
                val localVideos = generateLocalVideos()
                _uiState.update { 
                    it.copy(
                        videos = localVideos,
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
                // 더 많은 로컬 비디오 로드
                val moreVideos = generateLocalVideos(startIndex = _uiState.value.videos.size)
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

    // 로컬 비디오 파일 사용
    private fun generateLocalVideos(startIndex: Int = 0): List<ShortsVideo> {
        val localVideos = mutableListOf<ShortsVideo>()
        
        // 로컬 비디오 파일 경로들 (assets 폴더 기준)
        val localVideoFiles = listOf(
            "videos/video1.mp4",
            "videos/video2.mp4", 
            "videos/video3.mp4",
        )
        
        // 만약 로컬 파일이 없다면 기본 샘플 비디오 사용
        val fallbackVideoUrls = listOf(
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4"
        )
        
        for (i in startIndex until startIndex + 10) {
            // 로컬 파일 우선 사용, 없으면 fallback URL 사용
            val videoUrl = if (i < localVideoFiles.size) {
                "asset://${localVideoFiles[i % localVideoFiles.size]}"
            } else {
                fallbackVideoUrls[i % fallbackVideoUrls.size]
            }
            
            localVideos.add(
                ShortsVideo(
                    id = "local_video_$i",
                    videoUrl = videoUrl,
                    title = "로컬 숏폼 영상 #$i\n기부와 관련된 영상입니다",
                    username = "티끌유저${i + 1}",
                    likeCount = (100..10000).random(),
                    shareCount = (10..1000).random(),
                    viewCount = (1000..100000).random(),
                    hashtags = listOf("기부", "나눔", "봉사", "일상", "브이로그", "꿀팁").shuffled().take(3),
                    isLiked = false
                )
            )
        }
        
        return localVideos
    }
}
