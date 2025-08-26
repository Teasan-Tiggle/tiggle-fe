package com.ssafy.tiggle.domain.usecase.donation

import com.ssafy.tiggle.domain.entity.donation.DonationAccount
import com.ssafy.tiggle.domain.repository.DonationRepository
import javax.inject.Inject

class GetDonationAccountUseCase @Inject constructor(
    private val donationRepository: DonationRepository
) {
    suspend operator fun invoke(): Result<DonationAccount> {
        return donationRepository.getDonationAccount()
    }
}
