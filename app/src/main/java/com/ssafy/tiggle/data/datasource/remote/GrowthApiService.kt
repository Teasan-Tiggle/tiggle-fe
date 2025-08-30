package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.BaseResponse
import com.ssafy.tiggle.data.model.growth.ClickHeartResponseDto
import com.ssafy.tiggle.data.model.growth.GrowthResponseDto
import retrofit2.http.GET
import retrofit2.http.POST

interface GrowthApiService {
    @GET("donation/growth")
    suspend fun getGrowthResult(): BaseResponse<GrowthResponseDto>

    @POST("donation/heart")
    suspend fun clickHeart(
    ): BaseResponse<ClickHeartResponseDto>
}