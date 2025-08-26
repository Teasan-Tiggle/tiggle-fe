package com.ssafy.tiggle.presentation.ui.piggybank

import com.ssafy.tiggle.domain.entity.piggybank.MainAccount
import com.ssafy.tiggle.domain.entity.piggybank.MainAccountDetail
import com.ssafy.tiggle.domain.entity.piggybank.PiggyBank
import com.ssafy.tiggle.domain.entity.piggybank.PiggyBankAccount
import com.ssafy.tiggle.domain.entity.piggybank.PiggyBankEntry

data class PiggyBankState(
    val piggyBankAccount: PiggyBankAccount = PiggyBankAccount(),
    val mainAccount: MainAccount = MainAccount(),
    val piggyBank: PiggyBank = PiggyBank(),

    //계좌 여부
    val hasPiggyBank: Boolean = false,
    val hasLinkedAccount: Boolean = false,

    //바텀 시트
    val showEsgCategorySheet: Boolean = false,
    val tempSelectedCategoryId: Int? = null, // 시트에서 임시 선택

    //주계좌 상세보기
    val mainAccountDetail: MainAccountDetail = MainAccountDetail(),

    //저금통 내역 상세보기
    val changeList: List<PiggyBankEntry> = emptyList(),
    val dutchpayList: List<PiggyBankEntry> = emptyList(),
    val selectedTab: PiggyTab = PiggyTab.SpareChange,

    val isLoading: Boolean = false,
    // 전체 에러 메시지
    val errorMessage: String? = null
)

enum class PiggyTab(val label: String) { SpareChange("자투리"), DutchPay("더치페이") }
