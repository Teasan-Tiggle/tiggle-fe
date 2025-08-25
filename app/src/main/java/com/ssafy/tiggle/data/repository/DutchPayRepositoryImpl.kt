package com.ssafy.tiggle.data.repository

import com.ssafy.tiggle.data.datasource.remote.DutchPayApiService
import com.ssafy.tiggle.data.model.dutchpay.request.DutchPayRequestDto
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequest
import com.ssafy.tiggle.domain.repository.DutchPayRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DutchPayRepositoryImpl @Inject constructor(
    private val dutchPayApiService: DutchPayApiService
) : DutchPayRepository {

    override suspend fun createDutchPayRequest(request: DutchPayRequest): Result<Unit> {
        return try {
            val requestDto = DutchPayRequestDto(
                userIds = request.userIds,
                totalAmount = request.totalAmount,
                title = request.title,
                message = request.message,
                payMore = request.payMore
            )
            
            val response = dutchPayApiService.createDutchPayRequest(requestDto)
            
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("더치페이 요청 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
