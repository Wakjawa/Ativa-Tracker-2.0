package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FundAccountType {
    BANK_ACCOUNT,
    EWALLET,
    SAVINGS_POCKET,
    SALARY_DEDUCTION,
    CASH,
    OTHER
}

@Entity(tableName = "fund_accounts")
data class FundAccountEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val accountType: String, // FundAccountType name
    val colorHex: Long = 0xFF0284C7,
    val notes: String = "",
    val collectedAmount: Double = 0.0, // Money currently saved/collected in this account
    val orderIndex: Int = 0
)
