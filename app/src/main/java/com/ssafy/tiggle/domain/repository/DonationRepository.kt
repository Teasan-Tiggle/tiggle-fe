package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.domain.entity.donation.DonationHistory

interface DonationRepository {
    suspend fun getDonationHistory(): Result<List<DonationHistory>>
}
