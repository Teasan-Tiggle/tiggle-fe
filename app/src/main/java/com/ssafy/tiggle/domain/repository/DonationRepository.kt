package com.ssafy.tiggle.domain.repository

import com.ssafy.tiggle.domain.entity.donation.DonationAccount
import com.ssafy.tiggle.domain.entity.donation.DonationHistory
import com.ssafy.tiggle.domain.entity.donation.DonationRequest
import com.ssafy.tiggle.domain.entity.donation.DonationStatus
import com.ssafy.tiggle.domain.entity.donation.DonationStatusType
import com.ssafy.tiggle.domain.entity.donation.DonationSummary

interface DonationRepository {
    suspend fun getDonationHistory(): Result<List<DonationHistory>>
    suspend fun getDonationSummary(): Result<DonationSummary>
    suspend fun getDonationStatus(type: DonationStatusType): Result<DonationStatus>
    suspend fun getDonationAccount(): Result<DonationAccount>
    suspend fun createDonation(request: DonationRequest): Result<Unit>
}
