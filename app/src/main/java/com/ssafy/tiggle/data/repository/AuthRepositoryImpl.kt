package com.ssafy.tiggle.data.repository

import android.util.Log
import com.google.gson.Gson
import com.ssafy.tiggle.data.datasource.local.AuthDataSource
import com.ssafy.tiggle.data.datasource.remote.AuthApiService
import com.ssafy.tiggle.data.model.BaseResponse
import com.ssafy.tiggle.data.model.auth.request.LoginRequestDto
import com.ssafy.tiggle.data.model.auth.request.SignUpRequestDto
import com.ssafy.tiggle.domain.entity.auth.UserSignUp
import com.ssafy.tiggle.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val authDataSource: AuthDataSource
) : AuthRepository {

    override suspend fun signUpUser(userSignUp: UserSignUp): Result<Unit> {
        return try {
            val signUpRequest = SignUpRequestDto(
                email = userSignUp.email,
                password = userSignUp.password,
                name = userSignUp.name,
                universityId = userSignUp.universityId,
                departmentId = userSignUp.departmentId,
                studentId = userSignUp.studentId,
                phone = userSignUp.phone,
            )

            val response = authApiService.signUp(signUpRequest)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.result) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(body?.message ?: "알 수 없는 오류가 발생했습니다."))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> "잘못된 요청입니다. 입력 정보를 확인해주세요."
                    401 -> "인증이 필요합니다."
                    403 -> "접근 권한이 없습니다. 회원가입 정보를 확인해주세요."
                    404 -> "요청한 페이지를 찾을 수 없습니다."
                    409 -> "이미 등록된 정보입니다. 다른 정보로 시도해주세요."
                    500 -> "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                    502, 503, 504 -> "서버가 일시적으로 이용할 수 없습니다. 잠시 후 다시 시도해주세요."
                    else -> {
                        if (!errorBody.isNullOrEmpty()) {
                            try {
                                val errorResponse =
                                    Gson().fromJson(errorBody, BaseResponse::class.java)
                                errorResponse.message ?: "회원가입에 실패했습니다. (${response.code()})"
                            } catch (e: Exception) {
                                "회원가입에 실패했습니다. (${response.code()})"
                            }
                        } else {
                            "회원가입에 실패했습니다. (${response.code()})"
                        }
                    }
                }
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Log.e("AuthRepositoryImpl", "💥 네트워크 예외 발생: ${e.message}", e)
            Result.failure(Exception("네트워크 연결을 확인해주세요."))
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            val loginRequest = LoginRequestDto(
                email = email,
                password = password
            )

            val response = authApiService.login(loginRequest)

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null && body.result) {
                    val newAccess = stripBearer(response.headers()["Authorization"])
                    if (newAccess.isBlank()) {
                        Result.failure(Exception("인증 토큰을 받을 수 없습니다."))
                    } else {
                        authDataSource.saveAccessToken(newAccess)
                        Result.success(Unit)
                    }
                } else {
                    Result.failure(Exception(body?.message ?: "로그인에 실패했습니다."))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> "잘못된 요청입니다. 입력 정보를 확인해주세요."
                    401 -> "이메일 또는 비밀번호가 올바르지 않습니다."
                    403 -> "접근 권한이 없습니다."
                    404 -> "존재하지 않는 사용자입니다."
                    500 -> "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                    502, 503, 504 -> "서버가 일시적으로 이용할 수 없습니다. 잠시 후 다시 시도해주세요."
                    else -> {
                        if (!errorBody.isNullOrEmpty()) {
                            try {
                                val errorResponse =
                                    Gson().fromJson(errorBody, BaseResponse::class.java)
                                errorResponse.message ?: "로그인에 실패했습니다. (${response.code()})"
                            } catch (e: Exception) {
                                "로그인에 실패했습니다. (${response.code()})"
                            }
                        } else {
                            "로그인에 실패했습니다. (${response.code()})"
                        }
                    }
                }
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Result.failure(Exception("네트워크 연결을 확인해주세요."))
        }
    }

    private fun stripBearer(authHeader: String?): String {
        return authHeader?.removePrefix("Bearer ")?.trim() ?: ""
    }
}


