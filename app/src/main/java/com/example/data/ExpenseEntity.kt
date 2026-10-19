package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExpenseCategory {
    CAR_PAYMENT,
    MAINTENANCE,
    ROAD_TAX,
    INSURANCE,
    FUEL,
    TOLL,
    PARKING,
    DETAILING,
    ACCESSORIES,
    OTHER
}

enum class PeriodType {
    MONTHLY,
    YEARLY
}

@Entity(tableName = "expense_subjects")
data class ExpenseEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val monthlyAmount: Double,
    val rawAmount: Double,
    val periodType: String, // MONTHLY or YEARLY
    val category: String, // ExpenseCategory name
    val notes: String = "",
    val isEnabled: Boolean = true,
    val orderIndex: Int = 0,
    val isDefaultSubject: Boolean = false,
    val fundAccountId: String? = null // Reference to FundAccountEntity id
)
