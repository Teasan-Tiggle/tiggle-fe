package com.ssafy.tiggle.data.datasource.remote

import com.ssafy.tiggle.data.model.UserDto
import javax.inject.Inject

/**
 * User 원격 데이터 소스
 * API 호출을 담당하는 클래스
 */
class UserRemoteDataSource @Inject constructor(
    private val apiService: UserApiService
) {
    
    suspend fun getAllUsers(): Result<List<UserDto>> {
        return try {
            val response = apiService.getAllUsers()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("API 호출 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserById(id: Long): Result<UserDto?> {
        return try {
            val response = apiService.getUserById(id)
            if (response.isSuccessful) {
                Result.success(response.body())
            } else {
                Result.failure(Exception("API 호출 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createUser(user: UserDto): Result<UserDto> {
        return try {
            val response = apiService.createUser(user)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("사용자 생성 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateUser(user: UserDto): Result<UserDto> {
        return try {
            val response = apiService.updateUser(user.id, user)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("사용자 업데이트 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteUser(id: Long): Result<Unit> {
        return try {
            val response = apiService.deleteUser(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("사용자 삭제 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
