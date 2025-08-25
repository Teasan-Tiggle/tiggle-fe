package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.BaseResponse
import com.ssafy.tiggle.data.model.EmptyResponse
import com.ssafy.tiggle.data.model.dutchpay.request.DutchPayRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface DutchPayApiService {
    @POST("/api/dutchpay/requests")
    suspend fun createDutchPayRequest(
        @Body request: DutchPayRequestDto
    ): Response<BaseResponse<EmptyResponse>>
}
