package com.ssafy.tiggle.domain.entity.donation

data class DonationStatus(
    val planetAmount: Int,
    val peopleAmount: Int,
    val prosperityAmount: Int
)

enum class DonationStatusType {
    MY_DONATION,
    UNIVERSITY,
    ALL_UNIVERSITY
}
