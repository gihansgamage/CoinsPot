package com.gihansgamage.coinspot.domain.usecase

import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class CalculateGoalTimelineUseCase @Inject constructor() {
    operator fun invoke(
        targetPrice: Double,
        currentSaved: Double,
        dailySavingAmount: Double
    ): GoalTimeline {
        val remainingAmount = targetPrice - currentSaved

        if (dailySavingAmount <= 0) {
            return GoalTimeline(
                daysRemaining = -1,
                expectedEndDate = null,
                canAchieve = false
            )
        }

        val daysNeeded = (remainingAmount / dailySavingAmount).toInt()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, daysNeeded)

        return GoalTimeline(
            daysRemaining = daysNeeded,
            expectedEndDate = calendar.time,
            canAchieve = true
        )
    }

    data class GoalTimeline(
        val daysRemaining: Int,
        val expectedEndDate: Date?,
        val canAchieve: Boolean
    )
}