package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.SignUpRequestDto
import com.ssafy.tiggle.data.model.SignUpResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 사용자 관련 API 서비스
 */
interface UserApiService {
    
    /**
     * 회원가입
     * 
     * @param signUpRequest 회원가입 요청 데이터
     * @return Response<SignUpResponseDto> 회원가입 응답
     */
    @POST("auth/signup")
    suspend fun signUp(
        @Body signUpRequest: SignUpRequestDto
    ): Response<SignUpResponseDto>
}
