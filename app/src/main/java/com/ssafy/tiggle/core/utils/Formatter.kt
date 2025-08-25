package com.ssafy.tiggle.core.utils

import java.util.Locale

object Formatter {
    // 정수 금액 포맷팅: 50,000원
    fun formatCurrency(amount: Long, locale: Locale = Locale.KOREA): String =
        String.format(locale, "%,d원", amount)

}