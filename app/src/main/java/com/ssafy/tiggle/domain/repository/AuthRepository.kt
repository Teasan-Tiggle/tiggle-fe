package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.domain.entity.UserSignUp

interface AuthRepository {
    suspend fun signUpUser(userSignUp: UserSignUp): Result<Unit>
    suspend fun loginUser(email: String, password: String): Result<Unit>
}


