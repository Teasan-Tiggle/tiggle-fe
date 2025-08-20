package com.ssafy.tiggle.data.repository

import com.ssafy.tiggle.data.datasource.remote.UserApiService
import com.ssafy.tiggle.data.model.SignUpRequestDto
import com.ssafy.tiggle.domain.entity.User
import com.ssafy.tiggle.domain.entity.UserSignUp
import com.ssafy.tiggle.domain.repository.UserRepository
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
    
    override suspend fun signUpUser(userSignUp: UserSignUp): Result<User> {
        return try {
            // 도메인 엔티티를 DTO로 변환
            val signUpRequest = SignUpRequestDto(
                email = userSignUp.email,
                password = userSignUp.password,
                name = userSignUp.name,
                school = userSignUp.school,
                department = userSignUp.department,
                studentId = userSignUp.studentId
            )
            
            val response = userApiService.signUp(signUpRequest)
            if (response.isSuccessful) {
                val signUpResponse = response.body()
                if (signUpResponse?.success == true && signUpResponse.data != null) {
                    Result.success(signUpResponse.data.toDomain())
                } else {
                    Result.failure(Exception(signUpResponse?.message ?: "회원가입에 실패했습니다."))
                }
            } else {
                when (response.code()) {
                    400 -> Result.failure(Exception("잘못된 요청입니다."))
                    409 -> Result.failure(Exception("이미 등록된 이메일입니다."))
                    500 -> Result.failure(Exception("서버 오류가 발생했습니다."))
                    else -> Result.failure(Exception("회원가입에 실패했습니다. (${response.code()})"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
