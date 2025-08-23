package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.BaseResponse
import com.ssafy.tiggle.data.model.DepartmentDto
import com.ssafy.tiggle.data.model.UniversityDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 대학교/학과 관련 API 서비스
 */
interface UniversityApiService {

    /**
     * 모든 대학교 조회
     *
     * @return Response<BaseResponse<List<UniversityDto>>> 대학교 목록 응답
     */
    @GET("universities")
    suspend fun getUniversities(): Response<BaseResponse<List<UniversityDto>>>

    /**
     * 특정 대학교의 학과 목록 조회
     *
     * @param universityId 대학교 ID
     * @return Response<BaseResponse<List<DepartmentDto>>> 학과 목록 응답
     */
    @GET("universities/{universityId}/departments")
    suspend fun getDepartments(
        @Path("universityId") universityId: Long
    ): Response<BaseResponse<List<DepartmentDto>>>
}
