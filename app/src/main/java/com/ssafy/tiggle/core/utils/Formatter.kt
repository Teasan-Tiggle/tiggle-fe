package com.ssafy.tiggle.core.utils

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object Formatter {
    // 정수 금액 포맷팅: 50,000원
    fun formatCurrency(amount: Long, locale: Locale = Locale.KOREA): String =
        String.format(locale, "%,d원", amount)

    // ISO 8601 날짜/시간 포맷팅: "2025-08-26T01:25:21" -> "2025.08.26 01:25"
    fun formatDateTime(isoDateTime: String): String {
        return try {
            val dateTime = LocalDateTime.parse(isoDateTime)
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm")
            dateTime.format(formatter)
        } catch (e: DateTimeParseException) {
            // 파싱 실패 시 원본 문자열 반환
            isoDateTime
        }
    }

    // 간단한 날짜 포맷팅: "2025-08-26T01:25:21" -> "8월 26일"
    fun formatDateOnly(isoDateTime: String): String {
        return try {
            val dateTime = LocalDateTime.parse(isoDateTime)
            val formatter = DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA)
            dateTime.format(formatter)
        } catch (e: DateTimeParseException) {
            // 파싱 실패 시 원본 문자열 반환
            isoDateTime
        }
    }

    // 시간만 포맷팅: "2025-08-26T01:25:21" -> "오전 1:25"
    fun formatTimeOnly(isoDateTime: String): String {
        return try {
            val dateTime = LocalDateTime.parse(isoDateTime)
            val formatter = DateTimeFormatter.ofPattern("a h:mm", Locale.KOREA)
            dateTime.format(formatter)
        } catch (e: DateTimeParseException) {
            // 파싱 실패 시 원본 문자열 반환
            isoDateTime
        }
    }

    // 상대적 시간 표시: "방금", "5분 전", "3시간 전", "2일 전" 등
    fun formatRelativeTime(isoDateTime: String): String {
        return try {
            val dateTime = LocalDateTime.parse(isoDateTime)
            val now = LocalDateTime.now()
            val duration = java.time.Duration.between(dateTime, now)

            when {
                duration.toMinutes() < 1 -> "방금"
                duration.toMinutes() < 60 -> "${duration.toMinutes()}분 전"
                duration.toHours() < 24 -> "${duration.toHours()}시간 전"
                duration.toDays() < 7 -> "${duration.toDays()}일 전"
                else -> formatDateOnly(isoDateTime)
            }
        } catch (e: DateTimeParseException) {
            // 파싱 실패 시 원본 문자열 반환
            isoDateTime
        }
    }
}