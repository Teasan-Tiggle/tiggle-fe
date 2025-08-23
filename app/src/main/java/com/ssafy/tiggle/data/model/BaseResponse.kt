package com.ssafy.tiggle.data.model

/**
 * 모든 API 응답의 기본 클래스
 *
 * @param T 응답 데이터의 타입
 */
data class BaseResponse<T>(
    val result: Boolean,
    val message: String? = null,
    val data: T? = null,
)
