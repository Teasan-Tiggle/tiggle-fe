package com.ssafy.tiggle.presentation.ui.dutchpay

import com.ssafy.tiggle.domain.entity.dutchpay.DutchPayRequestDetail

data class DutchPayRequestDetailUiState(
    val isLoading: Boolean = false,
    val dutchPayDetail: DutchPayRequestDetail? = null,
    val errorMessage: String? = null,
    val isPaymentSuccess: Boolean = false
)
