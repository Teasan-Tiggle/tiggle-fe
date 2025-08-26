package com.ssafy.tiggle.domain.usecase.piggybank

import com.ssafy.tiggle.domain.entity.piggybank.PiggyBank
import com.ssafy.tiggle.domain.repository.PiggyBankRepository
import javax.inject.Inject

class SetPiggyBankSettingUseCase @Inject constructor(
    private val repository: PiggyBankRepository
) {
    suspend operator fun invoke(
        name: String? = null,
        targetAmount: Long? = null,
        autoDonation: Boolean? = null,
        autoSaving: Boolean? = null,
        esgCategory: Int? = null
    ): Result<PiggyBank> {
        return repository.setPiggyBankSetting(
            name,
            targetAmount,
            autoDonation,
            autoSaving,
            esgCategory
        )
    }
}
