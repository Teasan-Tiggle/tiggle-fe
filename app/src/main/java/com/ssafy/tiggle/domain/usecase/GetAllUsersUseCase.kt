package com.ssafy.tiggle.domain.usecase

import com.ssafy.tiggle.domain.entity.UserSummary
import com.ssafy.tiggle.domain.repository.UserRepository
import javax.inject.Inject

class GetAllUsersUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<List<UserSummary>> {
        return userRepository.getAllUsers()
    }
}


