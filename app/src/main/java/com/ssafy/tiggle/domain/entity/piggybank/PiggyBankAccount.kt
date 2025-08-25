package com.ssafy.tiggle.domain.entity.piggybank

data class PiggyBankAccount (
    val name:String="",
    val currentAmount:Long=0L,
    val lastWeekSavedAmount:Long=0L
)