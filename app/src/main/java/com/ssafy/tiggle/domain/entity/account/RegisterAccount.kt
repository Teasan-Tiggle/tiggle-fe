package com.ssafy.tiggle.domain.entity.account

data class RegisterAccount(
    val accountNum: String = "",
    val owner:String="",
    val code:Int=0,
    val attemptsLeft: Int=3,
    val bankName:String="",
    val date:String="",

    val accountNumError: String? = null,
    val codeError:String?=null
) {
    /** 숫자 외 문자를 제거한 정규화(붙여넣기 대비) */
    private fun sanitize(raw: String): String = raw.filter { it.isDigit() }

    /** 계좌번호 유효성 검사 */
    fun validateAccountNum(input: String = accountNum): String? = when {
        input.isBlank() -> "계좌번호를 입력해주세요."
        !input.all { it.isDigit() } -> "숫자만 입력할 수 있습니다."
        else -> null
    }

    /** 외부에서 raw 입력을 받아 정규화 + 검증해서 갱신 */
    fun withValidation(raw: String): RegisterAccount {
        val normalized = sanitize(raw)
        return copy(
            accountNum = normalized,
            accountNumError = validateAccountNum(normalized)
        )
    }

    /**
     * 특정 필드만 유효성 검사를 수행하고 업데이트된 인스턴스 반환
     */
    fun validateField(field: ValidationRegisterField): RegisterAccount {
        return when (field) {
            ValidationRegisterField.ACCOUNT -> copy(
                accountNumError = validateAccountNum(
                    accountNum
                )
            )
            ValidationRegisterField.CODE ->
                copy(codeError = validateCode(code.toString()))
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
}


enum class ValidationRegisterField {
    ACCOUNT,
    CODE
}
