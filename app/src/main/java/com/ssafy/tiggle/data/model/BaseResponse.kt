package com.ssafy.tiggle.data.model

/**
 * 모든 API 응답의 기본 클래스
 * 
 * @param T 응답 데이터의 타입
 */
data class BaseResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val errorCode: String? = null,
    val timestamp: String? = null
)
