package com.ssafy.tiggle.data.repository

import com.ssafy.tiggle.data.datasource.remote.PiggyBankApiService
import com.ssafy.tiggle.data.model.piggybank.request.CreatePiggyBankRequestDto
import com.ssafy.tiggle.data.model.piggybank.request.PrimaryAccountRequestDto
import com.ssafy.tiggle.data.model.piggybank.request.SendSMSRequestDto
import com.ssafy.tiggle.data.model.piggybank.request.VerificationCheckRequestDto
import com.ssafy.tiggle.data.model.piggybank.request.VerificationRequestDto
import com.ssafy.tiggle.data.model.piggybank.request.VerifySMSRequestDto
import com.ssafy.tiggle.data.model.piggybank.response.VerifySMSResponseDto
import com.ssafy.tiggle.data.model.piggybank.response.toDomain
import com.ssafy.tiggle.domain.entity.piggybank.AccountHolder
import com.ssafy.tiggle.domain.repository.PiggyBankRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PiggyBankRepositoryImpl @Inject constructor(
    private val piggyBankApiService: PiggyBankApiService
) : PiggyBankRepository {
    override suspend fun getAccountHolder(accountNo: String): Result<AccountHolder> {
        return try {
            val response = piggyBankApiService.getAccountHolder(accountNo)

            if (response.isSuccessful) {
                val body = response.body()
                if (body?.result == true && body.data != null) {
                    Result.success(body.data.toDomain())
                } else {
                    Result.failure(Exception(body?.message ?: "예금주 조회에 실패했습니다."))
                }
            } else {
                val msg = when (response.code()) {
                    400 -> "요청 형식이 올바르지 않습니다."
                    404 -> "해당 계좌를 찾을 수 없습니다."
                    409 -> "중복된 요청입니다."
                    500 -> "서버 오류가 발생했습니다."
                    else -> "예금주 조회에 실패했습니다. (${response.code()})"
                }
                Result.failure(Exception(msg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun requestOneWonVerification(accountNo: String): Result<Unit> {
        return try {
            val res =
                piggyBankApiService.requestOneWonVerification(VerificationRequestDto(accountNo = accountNo))
            if (res.result) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(res.message ?: "1원 송금이 실패했습니다. "))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun requestOneWonCheckVerification(
        accountNo: String,
        authCode: String
    ): Result<String> {
        return try {
            val res = piggyBankApiService.requestOneWonVerificationCheck(
                VerificationCheckRequestDto(accountNo = accountNo, authCode = authCode)
            )
            if (res.result && res.data != null) {
                Result.success(res.data.verificationToken)
            } else {
                Result.failure(Exception(res.message ?: "인증 코드 검증에 실패했습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerPrimaryAccount(
        accountNo: String,
        verificationToken: String
    ): Result<Unit> {
        return try {
            val res = piggyBankApiService.registerPrimaryAccount(
                PrimaryAccountRequestDto(accountNo, verificationToken)
            )
            if (res.result) Result.success(Unit)
            else Result.failure(Exception(res.message ?: "주 계좌 등록에 실패했습니다."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPiggyBank(
        name: String,
        targetAmount: Long,
        esgCategoryId: Int
    ): Result<Unit> {
        return try {
            val res = piggyBankApiService.createPiggyBank(
                CreatePiggyBankRequestDto(name, targetAmount, esgCategoryId)
            )
            if (res.result && res.data != null) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(res.message ?: "계좌 정보 보내기에 실패했습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendSMS(
        phone: String,
        purpose: String
    ): Result<Unit> {
        return try {
            val res = piggyBankApiService.sendSMS(
                SendSMSRequestDto(phone, purpose)
            )
            if (res.result && res.data != null) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(res.message ?: "인증번호 발송 요청을 보내기에 실패했습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifySMS(
        phone: String,
        code: String,
        purpose: String
    ): Result<VerifySMSResponseDto> {
        return try {
            val res = piggyBankApiService.verifySMS(
                VerifySMSRequestDto(phone, code, purpose)
            )
            if (res.result && res.data != null) {
                Result.success(res.data)
            } else {
                Result.failure(Exception(res.message ?: "인증 코드 검증에 실패했습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}