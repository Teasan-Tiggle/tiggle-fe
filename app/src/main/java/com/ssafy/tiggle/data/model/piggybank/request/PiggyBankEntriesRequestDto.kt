package com.ssafy.tiggle.data.model.piggybank.request

data class PiggyBankEntriesRequestDto(
    val type: String? = null,     // CHANGE, DUTCHPAY
    val cursor: String? = null,   // 커서 기반 페이징
    val size: Int? = null,        // 페이지 사이즈
    val from: String? = null,     // 조회 시작일 (yyyy-MM-dd)
    val to: String? = null,       // 조회 종료일 (yyyy-MM-dd)
    val sortKey: String? = null   // 정렬 기준 키
)