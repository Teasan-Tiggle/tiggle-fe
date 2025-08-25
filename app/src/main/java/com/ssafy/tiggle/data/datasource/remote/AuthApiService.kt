package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.BaseResponse
import com.ssafy.tiggle.data.model.auth.request.LoginRequestDto
import com.ssafy.tiggle.data.model.auth.request.SignUpRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 사용자 관련 API 서비스
 */
interface AuthApiService {

    /**
     * 회원가입
     *
     * @param signUpRequest 회원가입 요청 데이터
     * @return Response<SignUpResponseDto> 회원가입 응답
     */
    @POST("auth/join")
    suspend fun signUp(
        @Body signUpRequest: SignUpRequestDto
    ): Response<BaseResponse<Unit>>


    /**
     * 로그인
     *
     * @param loginRequest 로그인 요청 데이터
     * @return Response<BaseResponse<Unit>> 로그인 응답
     */
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequestDto
    ): Response<BaseResponse<Unit>>

    @POST("auth/reissue")
    suspend fun reissueTokenByCookie(): Response<BaseResponse<Unit>>


}
