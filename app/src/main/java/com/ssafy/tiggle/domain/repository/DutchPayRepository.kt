package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequestDetail
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequest

interface DutchPayRepository {
    suspend fun createDutchPayRequest(request: DutchPayRequest): Result<Unit>
    suspend fun getDutchPayRequestDetail(dutchPayId: Long): Result<DutchPayRequestDetail>
    suspend fun payDutchPay(dutchPayId: Long, payMore: Boolean): Result<Unit>
}
