package com.gihansgamage.coinspot.domain.usecase

import javax.inject.Inject

class CalculateDisposableIncomeUseCase @Inject constructor() {
    operator fun invoke(
        monthlyIncome: Double,
        livingCosts: Double,
        foodExpenses: Double,
        otherCosts: Double
    ): Double {
        val totalExpenses = livingCosts + foodExpenses + otherCosts
        return (monthlyIncome - totalExpenses).coerceAtLeast(0.0)
    }
}