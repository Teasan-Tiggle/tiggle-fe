package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.BaseResponse
import com.ssafy.tiggle.data.model.dutchpay.response.UserSummaryDto
import com.ssafy.tiggle.data.model.growth.GrowthResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface GrowthApiService {
    @GET("donation/growth")
    suspend fun getGrowthResult(): BaseResponse<GrowthResponseDto>
}