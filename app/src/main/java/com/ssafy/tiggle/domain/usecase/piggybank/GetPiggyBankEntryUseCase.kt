package com.ssafy.tiggle.domain.usecase.piggybank

import com.ssafy.tiggle.domain.entity.piggybank.PiggyBankEntry
import com.ssafy.tiggle.domain.repository.PiggyBankRepository
import javax.inject.Inject

class GetPiggyBankEntryUseCase @Inject constructor(
    private val repository: PiggyBankRepository
) {
    suspend operator fun invoke(
        type: String,
        cursor: String? = null,
        size: Int? = null,
        from: String? = null,
        to: String? = null,
        sortKey: String? = null
    ): Result<List<PiggyBankEntry>> {
        return repository.getPiggyBankEntries(
            type = type,
            cursor = cursor,
            size = size,
            from = from,
            to = to,
            sortKey = sortKey
        )
    }
}
