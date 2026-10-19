package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "commitment_settings")
data class CommitmentSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val carModelName: String = "Perodua Ativa AV 2-Tone",
    val carVariantColor: String = "Pearl Diamond White & Black Roof",
    val carPriceOtr: Double = 73400.0,
    val downPaymentPercent: Double = 10.0,
    val loanTenureYears: Int = 9,
    val loanInterestRate: Double = 3.2,
    val upfrontBookingAndMisc: Double = 1000.0,
    val emergencyBufferMonths: Int = 3,
    val currentSavedAmount: Double = 4500.0,
    val monthlySavingsCapacity: Double = 1000.0,
    val userMonthlyNetSalary: Double = 4500.0
)
