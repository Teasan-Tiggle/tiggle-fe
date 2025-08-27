package com.ssafy.tiggle.domain.usecase.dutchpay

import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayList
import com.ssafy.tiggle.domain.repository.DutchPayRepository
import javax.inject.Inject

class GetDutchPayListUseCase @Inject constructor(
    private val dutchPayRepository: DutchPayRepository
) {
    suspend operator fun invoke(tab: String, cursor: String? = null): Result<DutchPayList> {
        return dutchPayRepository.getDutchPayList(tab, cursor)
    }
}
