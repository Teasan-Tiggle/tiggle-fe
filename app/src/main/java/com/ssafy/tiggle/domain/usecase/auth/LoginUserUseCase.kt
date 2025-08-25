package com.ssafy.tiggle.domain.usecase.auth

import com.ssafy.tiggle.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * 로그인 UseCase
 *
 * 로그인 비즈니스 로직을 처리합니다.
 */
class LoginUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * 로그인 실행
     *
     * @param email 이메일
     * @param password 비밀번호
     * @return Result<Unit> 성공 시 Unit, 실패 시 에러
     */
    suspend operator fun invoke(email: String, password: String): Result<Unit> {
        // 입력값 검증
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(
                IllegalArgumentException("이메일과 비밀번호를 입력해주세요.")
            )
        }

        // 로그인 실행
        return authRepository.loginUser(email, password)
    }
}
