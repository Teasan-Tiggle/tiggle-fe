package com.ssafy.tiggle.domain.usecase

import com.ssafy.tiggle.domain.entity.User
import com.ssafy.tiggle.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 모든 사용자를 가져오는 UseCase
 * 비즈니스 로직을 캡슐화하고 단일 책임을 가집니다
 */
class GetAllUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<List<User>> {
        return userRepository.getAllUsers()
    }
}
