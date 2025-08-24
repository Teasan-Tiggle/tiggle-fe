package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.BaseResponse
import com.ssafy.tiggle.data.model.UserSummaryDto
import retrofit2.Response
import retrofit2.http.GET

/**
 * 사용자 조회 API 서비스
 */
interface UserApiService {

    /**
     * 전체 사용자 목록 조회
     */
    @GET("users/list")
    suspend fun getAllUsers(): Response<BaseResponse<List<UserSummaryDto>>>
}


