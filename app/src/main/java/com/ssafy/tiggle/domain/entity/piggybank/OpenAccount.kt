package com.ssafy.tiggle.domain.entity.piggybank


data class OpenAccount(
    val targetDonationAmount: Long = 0L,
    val piggyBankName: String = "",
    val certificateCode: String = "",
    val phoneNum: String = "",
    val attemptsLeft: Int = 3,

    val amountError: String? = null,
    val piggyBankNameError: String? = null,
    val codeError: String? = null,
    val phoneNumError: String? = null
) {
    /**
     * 목표 금액 유효성 검사
     */
    fun validateTargetDonationAmount(input: String): String? {
        return when {
            input.isBlank() -> "목표 금액을 입력해주세요."
            !input.matches(Regex("^[0-9]+$")) -> "숫자만 입력할 수 있습니다."
            input.toInt() <= 0 -> "0원 이상의 금액을 입력해주세요."
            input.toInt() > 100_000 -> "목표 금액은 10만원 이하여야 합니다."
            else -> null
        }
    }

    /**
     * 저금통 이름 유효성 검사
     */
    fun validatePiggyBankName(): String? {
        return when {
            piggyBankName.isBlank() -> "저금통 이름을 입력해주세요."
            !piggyBankName.matches(Regex("^[a-zA-Z0-9가-힣 _-]+$")) -> "저금통 이름에는 특수문자를 사용할 수 없습니다."
            else -> null
        }
    }

    /**
     * 코드 유효성 검사
     */
    fun validateCode(input: String): String? {
        return when {
            input.isBlank() -> "인증 코드를 입력해주세요."
            !input.matches(Regex("^[0-9]+$")) -> "숫자만 입력할 수 있습니다."
            else -> null
        }
    }

    fun validatePhoneNum(input: String): String? {
        return when {
            input.isBlank() -> "휴대폰 번호를 입력해주세요."
            !input.matches(Regex("^[0-9]+$")) -> "숫자만 입력할 수 있습니다."
            input.length != 11 -> "휴대폰 번호는 11자리여야 합니다."
            else -> null
        }
    }

    /**
     * 전체 유효성 검사를 수행하고 에러가 포함된 새로운 인스턴스 반환
     */
    fun withValidation(): OpenAccount {
        return this.copy(
            amountError = validateTargetDonationAmount(targetDonationAmount.toString()),
            piggyBankNameError = validatePiggyBankName(),
            codeError = validateCode(certificateCode.toString()),
            phoneNumError = validatePhoneNum(phoneNum)
        )
    }

    /**
     * 특정 필드만 유효성 검사를 수행하고 업데이트된 인스턴스 반환
     */
    fun validateField(field: ValidationField): OpenAccount {
        return when (field) {
            ValidationField.AMOUNT -> copy(
                amountError = validateTargetDonationAmount(
                    targetDonationAmount.toString()
                )
            )

            ValidationField.ACCOUNTNAME -> copy(piggyBankNameError = validatePiggyBankName())
            ValidationField.CODE ->
                copy(codeError = validateCode(certificateCode))

            ValidationField.PHONE ->
                copy(phoneNumError = validatePhoneNum(phoneNum))
        }
    }

}

enum class ValidationField {
    AMOUNT,
    ACCOUNTNAME,
    CODE,
    PHONE
}
