package com.ssafy.tiggle.domain.usecase.dutchpay

import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequestDetail
import com.ssafy.tiggle.domain.repository.DutchPayRepository
import javax.inject.Inject

class GetDutchPayRequestDetailUseCase @Inject constructor(
    private val dutchPayRepository: DutchPayRepository
) {
    suspend operator fun invoke(dutchPayId: Long): Result<DutchPayRequestDetail> {
        return dutchPayRepository.getDutchPayRequestDetail(dutchPayId)
    }
}
