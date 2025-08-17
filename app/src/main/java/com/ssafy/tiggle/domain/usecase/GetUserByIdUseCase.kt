package com.ssafy.tiggle.domain.usecase

import com.ssafy.tiggle.domain.entity.User
import com.ssafy.tiggle.domain.repository.UserRepository
import javax.inject.Inject

/**
 * 특정 사용자를 가져오는 UseCase
 */
class GetUserByIdUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(id: Long): User? {
        return userRepository.getUserById(id)
    }
}
