package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequestDetail
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequest
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPaySummary
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayList
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayDetail

interface DutchPayRepository {
    suspend fun createDutchPayRequest(request: DutchPayRequest): Result<Unit>
    suspend fun getDutchPayRequestDetail(dutchPayId: Long): Result<DutchPayRequestDetail>
    suspend fun getDutchPayDetail(dutchPayId: Long): Result<DutchPayDetail>
    suspend fun payDutchPay(dutchPayId: Long, payMore: Boolean): Result<Unit>
    suspend fun getDutchPaySummary(): Result<DutchPaySummary>
    suspend fun getDutchPayList(tab: String, cursor: String? = null): Result<DutchPayList>
}
