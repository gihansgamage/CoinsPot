package com.gihansgamage.coinspot.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "saving_goals")
data class SavingGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val startDate: LocalDate,
    val targetDate: LocalDate,
    val currency: String,
    val currencySymbol: String,
    val iconName: String = "savings",
    val description: String = "",
    val isCompleted: Boolean = false,
    val completedDate: LocalDate? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Calculate progress percentage
    val progressPercentage: Float
        get() = if (targetAmount > 0) {
            ((currentAmount / targetAmount) * 100).toFloat().coerceIn(0f, 100f)
        } else 0f

    // Calculate remaining amount
    val remainingAmount: Double
        get() = (targetAmount - currentAmount).coerceAtLeast(0.0)

    // Calculate days until target
    val daysUntilTarget: Long
        get() = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), targetDate)

    // Calculate total days for the goal
    val totalDays: Long
        get() = java.time.temporal.ChronoUnit.DAYS.between(startDate, targetDate)

    // Calculate daily saving needed (remaining amount / remaining days)
    val dailySavingNeeded: Double
        get() {
            val remainingDays = daysUntilTarget
            return if (remainingDays > 0 && remainingAmount > 0) {
                remainingAmount / remainingDays
            } else 0.0
        }

    // Check if goal is overdue
    val isOverdue: Boolean
        get() = LocalDate.now().isAfter(targetDate) && !isCompleted

    // Calculate new target date based on current savings rate
    fun calculateNewTargetDate(currentDailySavingRate: Double): LocalDate {
        if (currentDailySavingRate <= 0 || remainingAmount <= 0) {
            return targetDate
        }
        val daysNeeded = (remainingAmount / currentDailySavingRate).toLong()
        return LocalDate.now().plusDays(daysNeeded)
    }
}