package com.ssafy.tiggle.data.model.piggybank.request

data class PiggyBankSettingRequestDto(
    val name: String? = null,
    val targetAmount: Long? = null,
    val autoDonation: Boolean? = null,
    val autoSaving: Boolean? = null,
    val esgCategoryId: Int? = null,
)