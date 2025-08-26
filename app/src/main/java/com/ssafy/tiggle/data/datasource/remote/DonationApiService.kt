package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.BaseResponse
import com.ssafy.tiggle.data.model.donation.DonationHistoryDto
import retrofit2.Response
import retrofit2.http.GET

interface DonationApiService {
    @GET("/api/donation/history")
    suspend fun getDonationHistory(): Response<BaseResponse<List<DonationHistoryDto>>>
}
