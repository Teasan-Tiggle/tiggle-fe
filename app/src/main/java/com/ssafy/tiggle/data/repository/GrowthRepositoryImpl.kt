package com.ssafy.tiggle.data.repository

import com.ssafy.tiggle.data.datasource.remote.GrowthApiService
import com.ssafy.tiggle.data.datasource.remote.UserApiService
import com.ssafy.tiggle.data.model.growth.toDomain
import com.ssafy.tiggle.data.model.piggybank.response.toDomain
import com.ssafy.tiggle.domain.repository.UserRepository
import com.ssafy.tiggle.domain.entity.dutchpay.UserSummary
import com.ssafy.tiggle.domain.entity.growth.GrowthResult
import com.ssafy.tiggle.domain.repository.GrowthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrowthRepositoryImpl @Inject constructor(
    private val growthApiService: GrowthApiService
) : GrowthRepository {

    override suspend fun getGrowthResult(): Result<GrowthResult> {
        return try {
            val response = growthApiService.getGrowthResult()

            if (response.result && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message ?: "성장 정보 불러오기에 실패했습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
