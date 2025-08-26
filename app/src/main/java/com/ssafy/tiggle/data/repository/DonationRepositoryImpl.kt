package com.ssafy.tiggle.data.repository

import com.ssafy.tiggle.data.datasource.remote.DonationApiService
import com.ssafy.tiggle.data.model.donation.DonationRequestDto
import com.ssafy.tiggle.domain.entity.donation.DonationAccount
import com.ssafy.tiggle.domain.entity.donation.DonationCategory
import com.ssafy.tiggle.domain.entity.donation.DonationHistory
import com.ssafy.tiggle.domain.entity.donation.DonationRequest
import com.ssafy.tiggle.domain.entity.donation.DonationStatus
import com.ssafy.tiggle.domain.entity.donation.DonationStatusType
import com.ssafy.tiggle.domain.entity.donation.DonationSummary
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

    override suspend fun getDonationSummary(): Result<DonationSummary> {
        return try {
            val response = donationApiService.getDonationSummary()
            if (response.isSuccessful && response.body()?.result == true) {
                val dto = response.body()?.data
                dto?.let {
                    val summary = DonationSummary(
                        totalAmount = it.totalAmount,
                        monthlyAmount = it.monthlyAmount,
                        categoryCnt = it.categoryCnt,
                        universityRank = it.universityRank
                    )
                    Result.success(summary)
                } ?: Result.failure(Exception("Failed to fetch donation summary"))
            } else {
                Result.failure(Exception("Failed to fetch donation summary"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDonationStatus(type: DonationStatusType): Result<DonationStatus> {
        return try {
            val response = when (type) {
                DonationStatusType.MY_DONATION -> donationApiService.getMyDonationStatus()
                DonationStatusType.UNIVERSITY -> donationApiService.getUniversityDonationStatus()
                DonationStatusType.ALL_UNIVERSITY -> donationApiService.getAllUniversityDonationStatus()
            }

            if (response.isSuccessful && response.body()?.result == true) {
                val dto = response.body()?.data
                dto?.let {
                    val status = DonationStatus(
                        planetAmount = it.planetAmount,
                        peopleAmount = it.peopleAmount,
                        prosperityAmount = it.prosperityAmount
                    )
                    Result.success(status)
                } ?: Result.failure(Exception("Failed to fetch donation status"))
            } else {
                Result.failure(Exception("Failed to fetch donation status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDonationAccount(): Result<DonationAccount> {
        return try {
            val response = donationApiService.getDonationAccount()

            if (response.isSuccessful && response.body()?.result == true) {
                val dto = response.body()?.data
                if (dto != null) {
                    Result.success(
                        DonationAccount(
                            accountName = dto.accountName,
                            accountNo = dto.accountNo,
                            balance = dto.balance
                        )
                    )
                } else {
                    Result.failure(Exception("계좌 정보가 없습니다."))
                }
            } else {
                Result.failure(Exception(response.body()?.message ?: "알 수 없는 오류가 발생했습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createDonation(request: DonationRequest): Result<Unit> {
        return try {
            val dto = DonationRequestDto(
                category = request.category.name,
                amount = request.amount
            )

            val response = donationApiService.createDonation(dto)

            if (response.isSuccessful && response.body()?.result == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.message ?: "기부 처리 중 오류가 발생했습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
