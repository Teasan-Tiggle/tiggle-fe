package com.ssafy.tiggle.presentation.ui.shorts

data class ShortsVideo(
    val id: String,
    val videoUrl: String,
    val thumbnailUrl: String? = null,
    val title: String,
    val username: String,
    val userProfileUrl: String? = null,
    val likeCount: Int,
    val shareCount: Int,
    val viewCount: Int,
    val hashtags: List<String>,
    val isLiked: Boolean = false,
    val isFollowing: Boolean = false,
    val duration: Long = 0L // 밀리초
)
