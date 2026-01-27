package com.gihansgamage.coinspot.domain.usecase

import javax.inject.Inject

class CalculateDailySavingUseCase @Inject constructor() {
    operator fun invoke(
        disposableIncome: Double,
        savingStyle: String
    ): Double {
        val percentage = when (savingStyle.lowercase()) {
            "conservative" -> 0.05  // 5%
            "balanced" -> 0.10      // 10%
            "aggressive" -> 0.20    // 20%
            else -> 0.10            // Default to balanced
        }

        val monthlySaving = disposableIncome * percentage
        return (monthlySaving / 30).coerceAtLeast(0.0) // Daily amount
    }
}