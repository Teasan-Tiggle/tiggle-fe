package com.ssafy.tiggle.data.model.piggybank.response

import com.ssafy.tiggle.domain.entity.piggybank.EsgCategory
import com.ssafy.tiggle.domain.entity.piggybank.PiggyBank

data class PiggyBankSettingResponseDto(
    val id: Int,
    val name: String,
    val currentAmount: Long,
    val targetAmount: Long,
    val savingCount: Int,
    val donationCount: Int,
    val donationTotalAmount: Long,
    val autoDonation: Boolean,
    val autoSaving: Boolean,
    val esgCategory: EsgCategoryDto
)

data class EsgCategoryDto(
    val id: Int,
    val name: String,
    val description: String,
    val characterName: String
)

fun PiggyBankSettingResponseDto.toDomain(): PiggyBank =
    PiggyBank(
        id = id,
        name = name,
        currentAmount = currentAmount,
        targetAmount = targetAmount,
        savingCount = savingCount,
        donationCount = donationCount,
        donationTotalAmount = donationTotalAmount,
        autoDonation = autoDonation,
        autoSaving = autoSaving,
        esgCategory = esgCategory.toDomain()
    )

fun EsgCategoryDto.toDomain(): EsgCategory =
    EsgCategory(
        id = id,
        name = name,
        description = description,
        characterName = characterName
    )