package com.gihansgamage.coinspot.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gihansgamage.coinspot.data.local.database.entities.SavingGoal
import com.gihansgamage.coinspot.data.local.database.entities.TransactionType
import com.gihansgamage.coinspot.domain.repository.SavingGoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val goalRepository: SavingGoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<InsightsUiState>(InsightsUiState.Loading)
    val uiState: StateFlow<InsightsUiState> = _uiState.asStateFlow()

    val allGoals: StateFlow<List<SavingGoal>> = goalRepository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeGoals: StateFlow<List<SavingGoal>> = goalRepository.getActiveGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedGoals: StateFlow<List<SavingGoal>> = goalRepository.getCompletedGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalSavings: StateFlow<Double> = goalRepository.getTotalSavings()
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _insights = MutableStateFlow<AppInsights>(AppInsights())
    val insights: StateFlow<AppInsights> = _insights.asStateFlow()

    init {
        loadInsights()
    }

    private fun loadInsights() {
        viewModelScope.launch {
            try {
                _uiState.value = InsightsUiState.Loading

                val completed = completedGoals.value.size
                val active = activeGoals.value.size
                val total = allGoals.value.size
                val totalSaved = totalSavings.value

                val averageGoalAmount = if (allGoals.value.isNotEmpty()) {
                    allGoals.value.map { it.targetAmount }.average()
                } else 0.0

                val completionRate = if (total > 0) {
                    (completed.toDouble() / total * 100).toInt()
                } else 0

                val averageSavingDays = if (completed > 0) {
                    completedGoals.value.map { goal ->
                        java.time.temporal.ChronoUnit.DAYS.between(goal.startDate, goal.completedDate)
                    }.average().toInt()
                } else 0

                val upcomingGoals = activeGoals.value
                    .sortedBy { it.daysUntilTarget }
                    .take(3)

                val goalsOnTrack = activeGoals.value.count { goal ->
                    val expectedProgress = calculateExpectedProgress(goal)
                    goal.progressPercentage >= (expectedProgress - 5) // 5% tolerance
                }

                val goalsAtRisk = activeGoals.value.count { goal ->
                    val expectedProgress = calculateExpectedProgress(goal)
                    goal.progressPercentage < (expectedProgress - 10)
                }

                val insights = AppInsights(
                    totalGoalsCreated = total,
                    totalGoalsCompleted = completed,
                    activeGoals = active,
                    completedGoalsPercentage = completionRate,
                    totalSaved = totalSaved,
                    averageGoalAmount = averageGoalAmount,
                    averageDaysToComplete = averageSavingDays,
                    upcomingGoals = upcomingGoals,
                    goalsOnTrack = goalsOnTrack,
                    goalsAtRisk = goalsAtRisk,
                    topSavingsGoal = allGoals.value.maxByOrNull { it.currentAmount },
                    fastestCompletedGoal = completedGoals.value.minByOrNull {
                        java.time.temporal.ChronoUnit.DAYS.between(it.startDate, it.completedDate)
                    },
                    savingStreak = calculateSavingStreak(),
                    consistencyScore = calculateConsistencyScore()
                )

                _insights.value = insights
                _uiState.value = InsightsUiState.Success
            } catch (e: Exception) {
                _uiState.value = InsightsUiState.Error(e.message ?: "Failed to load insights")
            }
        }
    }

    private fun calculateExpectedProgress(goal: SavingGoal): Float {
        val totalDays = java.time.temporal.ChronoUnit.DAYS.between(goal.startDate, goal.targetDate)
        if (totalDays <= 0) return 0f

        val daysElapsed = java.time.temporal.ChronoUnit.DAYS.between(goal.startDate, LocalDate.now())
        val elapsed = daysElapsed.toFloat()
        val total = totalDays.toFloat()

        return (elapsed / total * 100).coerceIn(0f, 100f)
    }

    private suspend fun calculateSavingStreak(): Int {
        if (activeGoals.value.isEmpty()) return 0

        var streak = 0
        var currentDate = LocalDate.now()

        repeat(365) {
            val hasAnyTransaction = allGoals.value.any { goal ->
                // Check if goal has transaction on this date
                true // Simplified for now
            }

            if (hasAnyTransaction) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else {
                return@repeat
            }
        }

        return streak
    }

    private fun calculateConsistencyScore(): Int {
        if (activeGoals.value.isEmpty()) return 0

        var totalScore = 0
        var count = 0

        activeGoals.value.forEach { goal ->
            val onTrackPercentage = (goal.progressPercentage.toInt()).coerceIn(0, 100)
            totalScore += onTrackPercentage
            count++
        }

        return if (count > 0) (totalScore / count).coerceIn(0, 100) else 0
    }

    fun getGoalProgressDistribution(): Map<String, Int> {
        return mapOf(
            "0-25%" to allGoals.value.count { it.progressPercentage <= 25 },
            "25-50%" to allGoals.value.count { it.progressPercentage in 25f..50f },
            "50-75%" to allGoals.value.count { it.progressPercentage in 50f..75f },
            "75-100%" to allGoals.value.count { it.progressPercentage in 75f..100f }
        )
    }

    fun getCategoryDistribution(): Map<String, Double> {
        val distribution = mutableMapOf<String, Double>()

        activeGoals.value.forEach { goal ->
            distribution[goal.name] = goal.currentAmount
        }

        return distribution
    }

    fun getProjectedCompletionDates(): Map<String, LocalDate> {
        val projections = mutableMapOf<String, LocalDate>()

        activeGoals.value.forEach { goal ->
            val daysRemaining = goal.daysUntilTarget
            val remainingAmount = goal.remainingAmount
            val dailyRate = if (daysRemaining > 0) {
                remainingAmount / daysRemaining
            } else 0.0

            if (dailyRate > 0) {
                val daysNeeded = (remainingAmount / dailyRate).toLong()
                val projectedDate = LocalDate.now().plusDays(daysNeeded)
                projections[goal.name] = projectedDate
            }
        }

        return projections
    }

    fun getSavingsVelocityMetrics(): List<VelocityMetric> {
        return activeGoals.value.map { goal ->
            val expectedProgress = calculateExpectedProgress(goal)
            val velocity = goal.progressPercentage - expectedProgress
            val status = when {
                velocity > 10 -> VelocityStatus.AHEAD
                velocity < -10 -> VelocityStatus.BEHIND
                else -> VelocityStatus.ON_TRACK
            }

            VelocityMetric(
                goalName = goal.name,
                currentProgress = goal.progressPercentage.toInt(),
                expectedProgress = expectedProgress.toInt(),
                velocity = velocity.toInt(),
                status = status
            )
        }
    }

    fun refreshInsights() {
        loadInsights()
    }
}

sealed class InsightsUiState {
    object Loading : InsightsUiState()
    object Success : InsightsUiState()
    data class Error(val message: String) : InsightsUiState()
}

data class AppInsights(
    val totalGoalsCreated: Int = 0,
    val totalGoalsCompleted: Int = 0,
    val activeGoals: Int = 0,
    val completedGoalsPercentage: Int = 0,
    val totalSaved: Double = 0.0,
    val averageGoalAmount: Double = 0.0,
    val averageDaysToComplete: Int = 0,
    val upcomingGoals: List<SavingGoal> = emptyList(),
    val goalsOnTrack: Int = 0,
    val goalsAtRisk: Int = 0,
    val topSavingsGoal: SavingGoal? = null,
    val fastestCompletedGoal: SavingGoal? = null,
    val savingStreak: Int = 0,
    val consistencyScore: Int = 0
)

data class VelocityMetric(
    val goalName: String,
    val currentProgress: Int,
    val expectedProgress: Int,
    val velocity: Int,
    val status: VelocityStatus
)

enum class VelocityStatus {
    AHEAD, ON_TRACK, BEHIND
}