package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.domain.entity.User
import com.ssafy.tiggle.domain.entity.UserSignUp

/**
 * 사용자 관련 Repository 인터페이스
 *
 * 도메인 레이어에서 정의하는 사용자 데이터 접근 계약
 */
interface UserRepository {

    /**
     * 회원가입
     *
     * @param userSignUp 회원가입 데이터
     * @return Result<Unit> 성공 시 Unit, 실패 시 에러
     */
    suspend fun signUpUser(userSignUp: UserSignUp): Result<Unit>

    /**
     * 로그인
     *
     * @param email 이메일
     * @param password 비밀번호
     * @return Result<Unit> 성공 시 Unit, 실패 시 에러
     */
    suspend fun loginUser(email: String, password: String): Result<Unit>
}
