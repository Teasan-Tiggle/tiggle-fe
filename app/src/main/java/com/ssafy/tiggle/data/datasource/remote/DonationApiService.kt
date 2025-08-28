package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.BaseResponse
import com.ssafy.tiggle.data.model.EmptyResponse
import com.ssafy.tiggle.data.model.donation.DonationAccountDto
import com.ssafy.tiggle.data.model.donation.DonationHistoryDto
import com.ssafy.tiggle.data.model.donation.DonationRankDto
import com.ssafy.tiggle.data.model.donation.DonationRequestDto
import com.ssafy.tiggle.data.model.donation.DonationStatusDto
import com.ssafy.tiggle.data.model.donation.DonationSummaryDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DonationApiService {
    @GET("/api/donation/history")
    suspend fun getDonationHistory(): Response<BaseResponse<List<DonationHistoryDto>>>

    @GET("/api/donation/status/summary")
    suspend fun getDonationSummary(): Response<BaseResponse<DonationSummaryDto>>

    @GET("/api/donation/status")
    suspend fun getMyDonationStatus(): Response<BaseResponse<DonationStatusDto>>

    @GET("/api/donation/status/university")
    suspend fun getUniversityDonationStatus(): Response<BaseResponse<DonationStatusDto>>

    @GET("/api/donation/status/university/all")
    suspend fun getAllUniversityDonationStatus(): Response<BaseResponse<DonationStatusDto>>

    @GET("/api/donation")
    suspend fun getDonationAccount(): Response<BaseResponse<DonationAccountDto>>

    @POST("/api/donation")
    suspend fun createDonation(@Body request: DonationRequestDto): Response<BaseResponse<EmptyResponse>>

    @GET("/api/donation/rank/university")
    suspend fun getUniversityRanking(): Response<BaseResponse<List<DonationRankDto>>>

    @GET("/api/donation/rank/department")
    suspend fun getDepartmentRanking(): Response<BaseResponse<List<DonationRankDto>>>
}
