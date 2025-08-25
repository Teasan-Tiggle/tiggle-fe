package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequest

interface DutchPayRepository {
    suspend fun createDutchPayRequest(request: DutchPayRequest): Result<Unit>
}
