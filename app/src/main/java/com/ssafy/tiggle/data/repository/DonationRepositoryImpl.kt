package com.ssafy.tiggle.data.repository

import com.ssafy.tiggle.data.datasource.remote.DonationApiService
import com.ssafy.tiggle.domain.entity.donation.DonationCategory
import com.ssafy.tiggle.domain.entity.donation.DonationHistory
import com.ssafy.tiggle.domain.repository.DonationRepository
import javax.inject.Inject

class DonationRepositoryImpl @Inject constructor(
    private val donationApiService: DonationApiService
) : DonationRepository {
    
    override suspend fun getDonationHistory(): Result<List<DonationHistory>> {
        return try {
            val response = donationApiService.getDonationHistory()
            if (response.isSuccessful && response.body()?.result == true) {
                val donationHistoryList = response.body()?.data?.map { dto ->
                    DonationHistory(
                        category = DonationCategory.fromValue(dto.category),
                        donatedAt = dto.donatedAt,
                        amount = dto.amount,
                        title = dto.title
                    )
                } ?: emptyList()
                Result.success(donationHistoryList)
            } else {
                Result.failure(Exception("Failed to fetch donation history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
