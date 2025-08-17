package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.UserDto
import retrofit2.Response
import retrofit2.http.*

/**
 * User API 서비스 인터페이스
 * Retrofit을 사용한 네트워크 통신 정의
 */
interface UserApiService {
    
    @GET("users")
    suspend fun getAllUsers(): Response<List<UserDto>>
    
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<UserDto>
    
    @POST("users")
    suspend fun createUser(@Body user: UserDto): Response<UserDto>
    
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Long,
        @Body user: UserDto
    ): Response<UserDto>
    
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<Unit>
}
