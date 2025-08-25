package com.ssafy.tiggle.data.repository

import com.ssafy.tiggle.data.datasource.remote.UserApiService
import com.ssafy.tiggle.domain.repository.UserRepository
import com.ssafy.tiggle.domain.entity.dutchpay.UserSummary
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UserRepository 구현체
 *
 * 실제 데이터 소스(API)와 통신하여 회원가입 데이터를 처리합니다.
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userApiService: UserApiService
) : UserRepository {


    override suspend fun getAllUsers(): Result<List<UserSummary>> {
        return try {
            val response = userApiService.getAllUsers()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.result && body.data != null) {
                    Result.success(body.data.map { it.toDomain() })
                } else {
                    Result.failure(Exception(body?.message ?: "사용자 목록을 가져오지 못했습니다."))
                }
            } else {
                Result.failure(Exception("사용자 목록 조회 실패 (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("네트워크 오류: ${e.message}"))
        }
    }

}
