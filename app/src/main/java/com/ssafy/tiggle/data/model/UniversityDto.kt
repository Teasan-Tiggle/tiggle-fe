package com.ssafy.tiggle.data.model

import com.ssafy.tiggle.domain.entity.University

/**
 * 대학교 DTO (Data Transfer Object)
 * 
 * API 응답으로 받는 대학교 데이터 구조
 */
data class UniversityDto(
    val id: Long,
    val name: String
) {
    /**
     * DTO를 도메인 엔티티로 변환
     */
    fun toDomain(): University {
        return University(
            id = id,
            name = name
        )
    }
}
