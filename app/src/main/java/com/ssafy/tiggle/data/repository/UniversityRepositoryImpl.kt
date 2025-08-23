package com.ssafy.tiggle.data.repository

import android.util.Log
import com.google.gson.Gson
import com.ssafy.tiggle.data.datasource.remote.UniversityApiService
import com.ssafy.tiggle.data.model.BaseResponse
import com.ssafy.tiggle.domain.entity.Department
import com.ssafy.tiggle.domain.entity.University
import com.ssafy.tiggle.domain.repository.UniversityRepository
import javax.inject.Inject
import javax.inject.Singleton

/**
 * UniversityRepository 구현체
 *
 * 실제 데이터 소스(API)와 통신하여 대학교/학과 데이터를 처리합니다.
 */
@Singleton
class UniversityRepositoryImpl @Inject constructor(
    private val universityApiService: UniversityApiService
) : UniversityRepository {

    override suspend fun getUniversities(): Result<List<University>> {
        Log.d("UniversityRepositoryImpl", "🏫 대학교 목록 API 호출 시작")
        return try {
            Log.d("UniversityRepositoryImpl", "📤 대학교 목록 요청 전송 중...")
            val response = universityApiService.getUniversities()
            Log.d("UniversityRepositoryImpl", "📥 대학교 목록 응답 수신: isSuccessful=${response.isSuccessful}, code=${response.code()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("UniversityRepositoryImpl", "✅ HTTP 성공 - 응답 본문: $body")
                if (body != null && body.result && body.data != null) {
                    val universities = body.data.map { it.toDomain() }
                    Log.d("UniversityRepositoryImpl", "🎉 대학교 목록 조회 성공! 총 ${universities.size}개")
                    Result.success(universities)
                } else {
                    Log.d("UniversityRepositoryImpl", "❌ 서버 로직 실패: ${body?.message}")
                    Result.failure(Exception(body?.message ?: "대학교 목록을 불러올 수 없습니다."))
                }
            } else {
                Log.d("UniversityRepositoryImpl", "❌ HTTP 실패: ${response.code()} ${response.message()}")
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> "잘못된 요청입니다."
                    401 -> "인증이 필요합니다."
                    403 -> "접근 권한이 없습니다."
                    404 -> "대학교 목록을 찾을 수 없습니다."
                    500 -> "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                    502, 503, 504 -> "서버가 일시적으로 이용할 수 없습니다. 잠시 후 다시 시도해주세요."
                    else -> {
                        if (!errorBody.isNullOrEmpty()) {
                            try {
                                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                                errorResponse.message ?: "대학교 목록을 불러올 수 없습니다. (${response.code()})"
                            } catch (e: Exception) {
                                "대학교 목록을 불러올 수 없습니다. (${response.code()})"
                            }
                        } else {
                            "대학교 목록을 불러올 수 없습니다. (${response.code()})"
                        }
                    }
                }
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Log.e("UniversityRepositoryImpl", "💥 네트워크 예외 발생: ${e.message}", e)
            Result.failure(Exception("네트워크 연결을 확인해주세요."))
        }
    }

    override suspend fun getDepartments(universityId: Long): Result<List<Department>> {
        Log.d("UniversityRepositoryImpl", "🎓 학과 목록 API 호출 시작 (대학교 ID: $universityId)")
        return try {
            Log.d("UniversityRepositoryImpl", "📤 학과 목록 요청 전송 중...")
            val response = universityApiService.getDepartments(universityId)
            Log.d("UniversityRepositoryImpl", "📥 학과 목록 응답 수신: isSuccessful=${response.isSuccessful}, code=${response.code()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("UniversityRepositoryImpl", "✅ HTTP 성공 - 응답 본문: $body")
                if (body != null && body.result && body.data != null) {
                    val departments = body.data.map { it.toDomain() }
                    Log.d("UniversityRepositoryImpl", "🎉 학과 목록 조회 성공! 총 ${departments.size}개")
                    Result.success(departments)
                } else {
                    Log.d("UniversityRepositoryImpl", "❌ 서버 로직 실패: ${body?.message}")
                    Result.failure(Exception(body?.message ?: "학과 목록을 불러올 수 없습니다."))
                }
            } else {
                Log.d("UniversityRepositoryImpl", "❌ HTTP 실패: ${response.code()} ${response.message()}")
                val errorBody = response.errorBody()?.string()
                val message = when (response.code()) {
                    400 -> "잘못된 요청입니다."
                    401 -> "인증이 필요합니다."
                    403 -> "접근 권한이 없습니다."
                    404 -> "해당 대학교의 학과 목록을 찾을 수 없습니다."
                    500 -> "서버 내부 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
                    502, 503, 504 -> "서버가 일시적으로 이용할 수 없습니다. 잠시 후 다시 시도해주세요."
                    else -> {
                        if (!errorBody.isNullOrEmpty()) {
                            try {
                                val errorResponse = Gson().fromJson(errorBody, BaseResponse::class.java)
                                errorResponse.message ?: "학과 목록을 불러올 수 없습니다. (${response.code()})"
                            } catch (e: Exception) {
                                "학과 목록을 불러올 수 없습니다. (${response.code()})"
                            }
                        } else {
                            "학과 목록을 불러올 수 없습니다. (${response.code()})"
                        }
                    }
                }
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            Log.e("UniversityRepositoryImpl", "💥 네트워크 예외 발생: ${e.message}", e)
            Result.failure(Exception("네트워크 연결을 확인해주세요."))
        }
    }
}
