package com.ssafy.tiggle.domain.usecase.donation

import com.ssafy.tiggle.domain.entity.donation.DonationRank
import com.ssafy.tiggle.domain.repository.DonationRepository
import javax.inject.Inject

class GetDepartmentRankingUseCase @Inject constructor(
    private val donationRepository: DonationRepository
) {
    suspend operator fun invoke(): Result<List<DonationRank>> {
        return donationRepository.getDepartmentRanking()
    }
}
