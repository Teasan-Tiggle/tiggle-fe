package com.ssafy.tiggle.domain.usecase

import com.ssafy.tiggle.domain.entity.UserSignUp
import com.ssafy.tiggle.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * 회원가입 UseCase
 * 
 * 회원가입 비즈니스 로직을 처리합니다.
 */
class SignUpUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * 회원가입 실행
     * 
     * @param userSignUp 회원가입 데이터
     * @return Result<User> 성공 시 생성된 사용자 정보, 실패 시 에러
     */
    suspend operator fun invoke(userSignUp: UserSignUp): Result<Unit> {
        // 1. 유효성 검사
        val validatedData = userSignUp.withValidation()
        if (!validatedData.isValid()) {
            return Result.failure(
                IllegalArgumentException("입력 데이터가 유효하지 않습니다.")
            )
        }

        // 2. 회원가입 실행 후 결과 반환
        return authRepository.signUpUser(validatedData)
    }
}
