package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.domain.entity.User
import kotlinx.coroutines.flow.Flow

/**
 * User Repository 인터페이스
 * 도메인 레이어에서 정의하는 데이터 접근 규약
 * 실제 구현은 Data 레이어에서 처리
 */
interface UserRepository {
    
    /**
     * 모든 사용자 정보를 가져옵니다
     */
    fun getAllUsers(): Flow<List<User>>
    
    /**
     * 특정 사용자 정보를 가져옵니다
     */
    suspend fun getUserById(id: Long): User?
    
    /**
     * 사용자 정보를 저장합니다
     */
    suspend fun saveUser(user: User): Result<User>
    
    /**
     * 사용자 정보를 업데이트합니다
     */
    suspend fun updateUser(user: User): Result<User>
    
    /**
     * 사용자를 삭제합니다
     */
    suspend fun deleteUser(id: Long): Result<Unit>
}
