package com.example.ui

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val formatter = NumberFormat.getCurrencyInstance(Locale("ms", "MY")).apply {
        maximumFractionDigits = 2
        minimumFractionDigits = 2
    }

    fun formatRm(amount: Double): String {
        return try {
            "RM " + String.format(Locale.US, "%,.2f", amount)
        } catch (e: Exception) {
            "RM %.2f".format(amount)
        }
    }

    fun formatRmNoDecimals(amount: Double): String {
        return try {
            "RM " + String.format(Locale.US, "%,.0f", amount)
        } catch (e: Exception) {
            "RM %.0f".format(amount)
        }
    }
}
