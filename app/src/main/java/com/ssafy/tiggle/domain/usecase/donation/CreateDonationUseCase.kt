package com.ssafy.tiggle.domain.usecase.donation

import com.ssafy.tiggle.domain.entity.donation.DonationRequest
import com.ssafy.tiggle.domain.repository.DonationRepository
import javax.inject.Inject

class CreateDonationUseCase @Inject constructor(
    private val donationRepository: DonationRepository
) {
    suspend operator fun invoke(request: DonationRequest): Result<Unit> {
        return donationRepository.createDonation(request)
    }
}
