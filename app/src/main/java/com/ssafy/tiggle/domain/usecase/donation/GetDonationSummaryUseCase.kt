package com.ssafy.tiggle.domain.usecase.donation

import com.ssafy.tiggle.domain.entity.donation.DonationSummary
import com.ssafy.tiggle.domain.repository.DonationRepository
import javax.inject.Inject

class GetDonationSummaryUseCase @Inject constructor(
    private val donationRepository: DonationRepository
) {
    suspend operator fun invoke(): Result<DonationSummary> {
        return donationRepository.getDonationSummary()
    }
}
