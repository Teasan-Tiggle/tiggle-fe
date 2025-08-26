package com.ssafy.tiggle.domain.usecase.donation

import com.ssafy.tiggle.domain.entity.donation.DonationHistory
import com.ssafy.tiggle.domain.repository.DonationRepository
import javax.inject.Inject

class GetDonationHistoryUseCase @Inject constructor(
    private val donationRepository: DonationRepository
) {
    suspend operator fun invoke(): Result<List<DonationHistory>> {
        return donationRepository.getDonationHistory()
    }
}
