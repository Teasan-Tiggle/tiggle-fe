package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.BaseResponse
import com.ssafy.tiggle.data.model.EmptyResponse
import com.ssafy.tiggle.data.model.piggybank.request.CreatePiggyBankRequestDto
import com.ssafy.tiggle.data.model.piggybank.request.PiggyBankSettingRequestDto
import com.ssafy.tiggle.data.model.piggybank.request.PrimaryAccountRequestDto
import com.ssafy.tiggle.data.model.piggybank.request.SendSMSRequestDto
import com.ssafy.tiggle.data.model.piggybank.request.VerificationCheckRequestDto
import com.ssafy.tiggle.data.model.piggybank.request.VerificationRequestDto
import com.ssafy.tiggle.data.model.piggybank.request.VerifySMSRequestDto
import com.ssafy.tiggle.data.model.piggybank.response.AccountHolderResponseDto
import com.ssafy.tiggle.data.model.piggybank.response.CreatePiggyBankResponseDto
import com.ssafy.tiggle.data.model.piggybank.response.MainAccountResponseDto
import com.ssafy.tiggle.data.model.piggybank.response.PiggyBankAccountResponseDto
import com.ssafy.tiggle.data.model.piggybank.response.PiggyBankSettingResponseDto
import com.ssafy.tiggle.data.model.piggybank.response.VerificationCheckResponseDto
import com.ssafy.tiggle.data.model.piggybank.response.VerifySMSResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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

    @POST("piggybank")
    suspend fun createPiggyBank(
        @Body body: CreatePiggyBankRequestDto
    ): BaseResponse<CreatePiggyBankResponseDto>

    @POST("auth/sms/send")
    suspend fun sendSMS(
        @Body body: SendSMSRequestDto
    ): BaseResponse<Unit>

    @POST("auth/sms/verify")
    suspend fun verifySMS(
        @Body body: VerifySMSRequestDto
    ): BaseResponse<VerifySMSResponseDto>

    @GET("accounts/primary")
    suspend fun getMainAccount(): BaseResponse<MainAccountResponseDto>

    @GET("piggybank/summary")
    suspend fun getPiggyBankAccount(): BaseResponse<PiggyBankAccountResponseDto>

    @PATCH("piggybank/settings")
    suspend fun setPiggyBankSetting(
        @Body body: PiggyBankSettingRequestDto
    ): BaseResponse<PiggyBankSettingResponseDto>

    @PUT("piggybank/category/{categoryId}")
    suspend fun setEsgCategory(
        @Path("categoryId") categoryId: Int
    ): BaseResponse<PiggyBankSettingResponseDto>
}
