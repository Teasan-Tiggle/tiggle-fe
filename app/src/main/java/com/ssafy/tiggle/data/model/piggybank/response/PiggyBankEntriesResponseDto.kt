package com.ssafy.tiggle.data.model.piggybank.response

import com.ssafy.tiggle.domain.entity.piggybank.PiggyBankEntry

data class PiggyBankEntriesResponseDto(
    val items: List<PiggyBankEntryItem>,
    val nextCursor: String?,
    val size: Int,
    val hasNext: Boolean
) {

}

data class PiggyBankEntryItem(
    val id: String,
    val type: String,
    val amount: Long,
    val occurredDate: String,
    val title: String
)

fun PiggyBankEntriesResponseDto.toDomain(): List<PiggyBankEntry> =
    items.map {
        it.toDomain()
    }


fun PiggyBankEntryItem.toDomain(): PiggyBankEntry =
    PiggyBankEntry(
        id = id,
        type = type,
        amount = amount,
        occurredAt = occurredDate,
        title = title
    )