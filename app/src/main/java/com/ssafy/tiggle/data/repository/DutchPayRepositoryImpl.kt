package com.ssafy.tiggle.data.repository

import com.ssafy.tiggle.data.datasource.remote.DutchPayApiService
import com.ssafy.tiggle.data.model.dutchpay.request.DutchPayRequestDto
import com.ssafy.tiggle.data.model.dutchpay.request.DutchPayPaymentRequestDto
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequest
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequestDetail
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPaySummary
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayList
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayItem
import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayDetail
import com.ssafy.tiggle.domain.entity.dutchpay.Creator
import com.ssafy.tiggle.domain.entity.dutchpay.Share
import com.ssafy.tiggle.domain.repository.DutchPayRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DutchPayRepositoryImpl @Inject constructor(
    private val dutchPayApiService: DutchPayApiService
) : DutchPayRepository {

    override suspend fun createDutchPayRequest(request: DutchPayRequest): Result<Unit> {
        return try {
            val requestDto = DutchPayRequestDto(
                userIds = request.userIds,
                totalAmount = request.totalAmount,
                title = request.title,
                message = request.message,
                payMore = request.payMore
            )

            val response = dutchPayApiService.createDutchPayRequest(requestDto)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("더치페이 요청 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDutchPayRequestDetail(dutchPayId: Long): Result<DutchPayRequestDetail> {
        return try {
            val response = dutchPayApiService.getDutchPayRequestDetail(dutchPayId)

            if (response.isSuccessful) {
                val responseData = response.body()?.data
                if (responseData != null) {
                    val detail = DutchPayRequestDetail(
                        dutchPayId = responseData.dutchpayId,
                        title = responseData.title,
                        message = responseData.message,
                        requesterName = responseData.requesterName,
                        participantCount = responseData.participantCount,
                        totalAmount = responseData.totalAmount,
                        requestedAt = responseData.requestedAt,
                        myAmount = responseData.myAmount,
                        originalAmount = responseData.originalAmount,
                        tiggleAmount = responseData.tiggleAmount,
                        payMoreDefault = responseData.payMoreDefault,
                        isCreator = responseData.creator
                    )
                    Result.success(detail)
                } else {
                    Result.failure(Exception("응답 데이터가 없습니다"))
                }
            } else {
                Result.failure(Exception("더치페이 상세 조회 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDutchPayDetail(dutchPayId: Long): Result<DutchPayDetail> {
        return try {
            val response = dutchPayApiService.getDutchPayDetail(dutchPayId)

            if (response.isSuccessful) {
                val responseData = response.body()?.data
                if (responseData != null) {
                    val detail = DutchPayDetail(
                        id = responseData.id,
                        title = responseData.title,
                        message = responseData.message,
                        totalAmount = responseData.totalAmount,
                        status = responseData.status,
                        creator = Creator(
                            id = responseData.creator.id,
                            name = responseData.creator.name
                        ),
                        shares = responseData.shares.map { shareDto ->
                            Share(
                                userId = shareDto.userId,
                                name = shareDto.name,
                                amount = shareDto.amount,
                                status = shareDto.status
                            )
                        },
                        roundedPerPerson = responseData.roundedPerPerson,
                        payMore = responseData.payMore,
                        createdAt = responseData.createdAt
                    )
                    Result.success(detail)
                } else {
                    Result.failure(Exception("응답 데이터가 없습니다"))
                }
            } else {
                Result.failure(Exception("더치페이 상세 조회 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun payDutchPay(dutchPayId: Long, payMore: Boolean): Result<Unit> {
        return try {
            val requestDto = DutchPayPaymentRequestDto(payMore = payMore)
            val response = dutchPayApiService.payDutchPay(dutchPayId, requestDto)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("송금 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDutchPaySummary(): Result<DutchPaySummary> {
        return try {
            val response = dutchPayApiService.getDutchPaySummary()

            if (response.isSuccessful) {
                val responseData = response.body()?.data
                if (responseData != null) {
                    val summary = DutchPaySummary(
                        totalTransferredAmount = responseData.totalTransferredAmount,
                        transferCount = responseData.transferCount,
                        participatedCount = responseData.participatedCount
                    )
                    Result.success(summary)
                } else {
                    Result.failure(Exception("응답 데이터가 없습니다"))
                }
            } else {
                Result.failure(Exception("더치페이 현황 조회 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDutchPayList(tab: String, cursor: String?): Result<DutchPayList> {
        return try {
            val response = dutchPayApiService.getDutchPayList(tab, cursor)

            if (response.isSuccessful) {
                val responseData = response.body()?.data
                if (responseData != null) {
                    val items = responseData.items.map { itemDto ->
                        DutchPayItem(
                            dutchpayId = itemDto.dutchpayId,
                            title = itemDto.title,
                            myAmount = itemDto.myAmount,
                            totalAmount = itemDto.totalAmount,
                            participantCount = itemDto.participantCount,
                            paidCount = itemDto.paidCount,
                            requestedAt = itemDto.requestedAt,
                            isCreator = itemDto.isCreator,
                            creatorName = itemDto.creatorName,
                            tiggleAmount = itemDto.tiggleAmount
                        )
                    }
                    
                    val list = DutchPayList(
                        items = items,
                        nextCursor = responseData.nextCursor,
                        hasNext = responseData.hasNext
                    )
                    Result.success(list)
                } else {
                    Result.failure(Exception("응답 데이터가 없습니다"))
                }
            } else {
                Result.failure(Exception("더치페이 내역 조회 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
