package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.data.model.piggybank.response.VerifySMSResponseDto
import com.ssafy.tiggle.domain.entity.piggybank.AccountHolder

interface PiggyBankRepository {
    suspend fun getAccountHolder(accountNo: String): Result<AccountHolder>
    suspend fun requestOneWonVerification(accountNo: String): Result<Unit>
    suspend fun requestOneWonCheckVerification(accountNo: String, authCode: String): Result<String>
    suspend fun registerPrimaryAccount(
        accountNo: String,
        verificationToken: String
    ): Result<Unit>

    suspend fun createPiggyBank(name: String, targetAmount: Long, esgCategoryId: Int): Result<Unit>
    suspend fun sendSMS(phone: String, purpose: String): Result<Unit>
    suspend fun verifySMS(phone: String, code: String, purpose: String): Result<VerifySMSResponseDto>
}