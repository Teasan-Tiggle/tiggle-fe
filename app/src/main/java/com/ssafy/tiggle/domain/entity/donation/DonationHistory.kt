package com.ssafy.tiggle.domain.entity.donation

data class DonationHistory(
    val category: DonationCategory,
    val donatedAt: String,
    val amount: Int,
    val title: String
)

enum class DonationCategory(val value: String, val iconResName: String) {
    PLANET("planet", "planet"),
    PEOPLE("people", "people"), 
    PROSPERITY("prosperity", "prosperity");
    
    companion object {
        fun fromValue(value: String): DonationCategory {
            return values().find { it.value == value } ?: PLANET
        }
    }
}
