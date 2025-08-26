package com.ssafy.tiggle.domain.usecase.donation

import com.ssafy.tiggle.domain.entity.donation.DonationStatus
import com.ssafy.tiggle.domain.entity.donation.DonationStatusType
import com.ssafy.tiggle.domain.repository.DonationRepository
import javax.inject.Inject

class GetDonationStatusUseCase @Inject constructor(
    private val donationRepository: DonationRepository
) {
    suspend operator fun invoke(type: DonationStatusType): Result<DonationStatus> {
        return donationRepository.getDonationStatus(type)
    }
}
