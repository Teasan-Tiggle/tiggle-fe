package com.ssafy.tiggle.domain.usecase

import com.ssafy.tiggle.domain.entity.User
import com.ssafy.tiggle.domain.repository.UserRepository
import javax.inject.Inject

/**
 * 사용자를 저장하는 UseCase
 */
class SaveUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<User> {
        // 비즈니스 규칙 검증
        if (user.name.isBlank()) {
            return Result.failure(IllegalArgumentException("사용자 이름은 비어있을 수 없습니다"))
        }
        
        if (user.email.isBlank() || !isValidEmail(user.email)) {
            return Result.failure(IllegalArgumentException("유효한 이메일 주소를 입력해주세요"))
        }
        
        return userRepository.saveUser(user)
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
