package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.data.model.piggybank.response.VerifySMSResponseDto
import com.ssafy.tiggle.domain.entity.piggybank.AccountHolder
import com.ssafy.tiggle.domain.entity.piggybank.MainAccount
import com.ssafy.tiggle.domain.entity.piggybank.MainAccountDetail
import com.ssafy.tiggle.domain.entity.piggybank.PiggyBank
import com.ssafy.tiggle.domain.entity.piggybank.PiggyBankAccount

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
    suspend fun verifySMS(
        phone: String,
        code: String,
        purpose: String
    ): Result<VerifySMSResponseDto>

    suspend fun getMainAccount(): Result<MainAccount>
    suspend fun getPiggyBankAccount(): Result<PiggyBankAccount>
    suspend fun setPiggyBankSetting(
        name: String?,
        targetAmount: Long?,
        autoDonation: Boolean?,
        autoSaving: Boolean?,
        esgCategory: Int?
    ): Result<PiggyBank>

    suspend fun setEsgCategory(categoryId: Int): Result<PiggyBank>
    suspend fun getTransactions(
        accountNo: String,
        cursor: String? = null
    ): Result<MainAccountDetail>
}