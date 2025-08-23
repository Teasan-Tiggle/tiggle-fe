package com.ssafy.tiggle.presentation.ui.piggybank

data class PiggyBankState(
    val isLoading: Boolean = false,

    //계좌 여부
    val hasPiggyBank: Boolean = false,
    val hasLinkedAccount: Boolean = false,

    //배너/ 계좌 카드 데이터 (임시로 만들었음 수정 예정)
    val todaySaving: Int = 0,                // 오늘 모인 띠끌
    val lastWeekRemainder: Int = 0,          // 지난주 잔액 예: 15847
    val lastWeekRounded: Int = 0,            // 지난주 목표 반올림 금액 예: 15000
    val accountBank: String? = null,         // 예: "신한"
    val accountName: String? = null,         // 예: "쏠편한 입출금통장(저축예금)"
    val accountNumberMasked: String? = null, // 예: "123-456-789000"
    val accountBalance: Int? = null,         // 예: 100000
    // 토글
    val changeLeftoverDonate: Boolean = false,
    val achieveGoalDonate: Boolean = false,
    // 전체 에러 메시지
    val generalError: String? = null
)