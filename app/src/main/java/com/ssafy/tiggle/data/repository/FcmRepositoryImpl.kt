package com.ssafy.tiggle.data.repository

import android.util.Log
import com.ssafy.tiggle.data.datasource.remote.FcmApiService
import com.ssafy.tiggle.data.model.fcm.FcmTokenRequestDto
import com.ssafy.tiggle.domain.repository.FcmRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmRepositoryImpl @Inject constructor(
    private val fcmApiService: FcmApiService
) : FcmRepository {
    override suspend fun registerToken(
        token: String,
    ): Result<Unit> {
        return try {
            val res = fcmApiService.registerToken(
                FcmTokenRequestDto(
                    fcmToken = token,
                )
            )
            Log.d(
                "FcmRepository",
                "⬅️ /fcm/token result=${res.result}, message=${res.message}"
            )
            if (res.result) Result.success(Unit)
            else Result.failure(Exception(res.message))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}