package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.BaseResponse
import com.ssafy.tiggle.data.model.EmptyResponse
import com.ssafy.tiggle.data.model.dutchpay.request.DutchPayPaymentRequestDto
import com.ssafy.tiggle.data.model.dutchpay.request.DutchPayRequestDto
import com.ssafy.tiggle.data.model.dutchpay.response.DutchPayRequestDetailResponseDto
import com.ssafy.tiggle.data.model.dutchpay.response.DutchPaySummaryResponseDto
import com.ssafy.tiggle.data.model.dutchpay.response.DutchPayListResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DutchPayApiService {
    @POST("/api/dutchpay/requests")
    suspend fun createDutchPayRequest(
        @Body request: DutchPayRequestDto
    ): Response<BaseResponse<EmptyResponse>>

    @GET("/api/dutchpay/requests/{id}")
    suspend fun getDutchPayRequestDetail(
        @Path("id") dutchPayId: Long
    ): Response<BaseResponse<DutchPayRequestDetailResponseDto>>

    @POST("/api/dutchpay/requests/{dutchpayId}/pay")
    suspend fun payDutchPay(
        @Path("dutchpayId") dutchPayId: Long,
        @Body request: DutchPayPaymentRequestDto
    ): Response<BaseResponse<EmptyResponse>>

    @GET("/api/dutchpay/requests/summary")
    suspend fun getDutchPaySummary(): Response<BaseResponse<DutchPaySummaryResponseDto>>

    @GET("/api/dutchpay/requests/list")
    suspend fun getDutchPayList(
        @Query("tab") tab: String,
        @Query("cursor") cursor: String? = null
    ): Response<BaseResponse<DutchPayListResponseDto>>
}
