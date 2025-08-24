package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.BaseResponse
import com.ssafy.tiggle.data.model.EmptyResponse
import com.ssafy.tiggle.data.model.piggybank.request.PrimaryAccountRequestDto
import com.ssafy.tiggle.data.model.piggybank.request.VerificationCheckRequestDto
import com.ssafy.tiggle.data.model.piggybank.request.VerificationRequestDto
import com.ssafy.tiggle.data.model.piggybank.response.AccountHolderResponseDto
import com.ssafy.tiggle.data.model.piggybank.response.VerificationCheckResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PiggyBankApiService {
    @GET("accounts/holder")
    suspend fun getAccountHolder(
        @Query("accountNo") accountNo: String
    ): Response<BaseResponse<AccountHolderResponseDto>>

    @POST("accounts/verification")
    suspend fun requestOneWonVerification(
        @Body body: VerificationRequestDto
    ): BaseResponse<EmptyResponse>

    @POST("accounts/verification/check")
    suspend fun requestOneWonVerificationCheck(
        @Body body: VerificationCheckRequestDto
    ): BaseResponse<VerificationCheckResponseDto>

    @POST("accounts/primary")
    suspend fun registerPrimaryAccount(
        @Body body: PrimaryAccountRequestDto
    ): BaseResponse<EmptyResponse>
}
//0888315782686732