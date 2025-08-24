package com.ssafy.tiggle.domain.usecase.piggybank

import javax.inject.Inject

data class PiggyBankUseCases @Inject constructor(
    val getAccountHolderUseCase: GetAccountHolderUseCase,
    val requestOneWonVerificationUseCase: RequestOneWonVerificationUseCase,
    val requestOneWonCheckVerificationUseCase: RequestOneWonCheckVerificationUseCase,
    val registerPrimaryAccountUseCase: RegisterPrimaryAccountUseCase,
    val createPiggyBankUseCase: CreatePiggyBankUseCase,
    val sendSMSUseCase: SendSMSUseCase,
    val verifySMSUseCase: VerifySMSUseCase
)