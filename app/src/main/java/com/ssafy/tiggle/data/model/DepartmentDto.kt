package com.ssafy.tiggle.data.model

import com.ssafy.tiggle.domain.entity.Department

/**
 * 학과 DTO (Data Transfer Object)
 * 
 * API 응답으로 받는 학과 데이터 구조
 */
data class DepartmentDto(
    val id: Long,
    val name: String
) {
    /**
     * DTO를 도메인 엔티티로 변환
     */
    fun toDomain(): Department {
        return Department(
            id = id,
            name = name
        )
    }
}
