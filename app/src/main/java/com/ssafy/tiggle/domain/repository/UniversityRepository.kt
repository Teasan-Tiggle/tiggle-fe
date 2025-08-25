package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.domain.entity.auth.Department
import com.ssafy.tiggle.domain.entity.auth.University

/**
 * 대학교/학과 관련 Repository 인터페이스
 *
 * 도메인 레이어에서 정의하는 대학교/학과 데이터 접근 계약
 */
interface UniversityRepository {

    /**
     * 모든 대학교 조회
     *
     * @return Result<List<University>> 성공 시 대학교 목록, 실패 시 에러
     */
    suspend fun getUniversities(): Result<List<University>>

    /**
     * 특정 대학교의 학과 목록 조회
     *
     * @param universityId 대학교 ID
     * @return Result<List<Department>> 성공 시 학과 목록, 실패 시 에러
     */
    suspend fun getDepartments(universityId: Long): Result<List<Department>>
}
