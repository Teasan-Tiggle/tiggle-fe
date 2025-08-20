package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.domain.entity.User
import com.ssafy.tiggle.domain.entity.UserSignUp

/**
 * 회원가입 관련 Repository 인터페이스
 * 
 * 도메인 레이어에서 정의하는 회원가입 데이터 접근 계약
 */
interface UserRepository {
    
    /**
     * 회원가입
     * 
     * @param userSignUp 회원가입 데이터
     * @return Result<User> 성공 시 생성된 사용자 정보, 실패 시 에러
     */
    suspend fun signUpUser(userSignUp: UserSignUp): Result<User>
}
