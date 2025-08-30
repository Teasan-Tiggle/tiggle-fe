package com.ssafy.tiggle.data.repository

import com.ssafy.tiggle.data.datasource.remote.GrowthApiService
import com.ssafy.tiggle.data.model.growth.toDomain
import com.ssafy.tiggle.domain.entity.growth.GrowthResult
import com.ssafy.tiggle.domain.entity.growth.HeartResult
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

    override suspend fun clickHeart(): Result<HeartResult> {
        return try {
            val response = growthApiService.clickHeart()

            if (response.result && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message ?: "하트 클릭에 실패했습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
