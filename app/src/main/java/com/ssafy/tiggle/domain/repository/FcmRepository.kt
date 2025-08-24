package com.ssafy.tiggle.domain.repository

interface FcmRepository {
    suspend fun registerToken(token: String): Result<Unit>
}