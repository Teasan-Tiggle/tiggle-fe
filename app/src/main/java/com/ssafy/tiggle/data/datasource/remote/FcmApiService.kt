package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.BaseResponse
import com.ssafy.tiggle.data.model.EmptyResponse
import com.ssafy.tiggle.data.model.fcm.FcmTokenRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmApiService {
    @POST("fcm/token")
    suspend fun registerToken(
        @Body body: FcmTokenRequestDto
    ): BaseResponse<EmptyResponse>
}