package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.domain.entity.UserSummary

/**
 * 사용자 관련 Repository 인터페이스
 *
 * 도메인 레이어에서 정의하는 사용자 데이터 접근 계약
 */
interface UserRepository {

    /**
     * 전체 사용자 목록 조회 (간단 정보)
     */
    suspend fun getAllUsers(): Result<List<UserSummary>>
}
