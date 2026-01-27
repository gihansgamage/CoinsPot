package com.gihansgamage.coinspot.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "saving_goals")
data class EnhancedSavingGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // Basic Information
    val name: String,
    val description: String = "",
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val startDate: LocalDate,
    val targetDate: LocalDate,

    // Currency
    val currency: String,
    val currencySymbol: String,

    // Visual & Categorization
    val iconName: String = "savings",
    val imageUrl: String? = null,
    val category: GoalCategory = GoalCategory.OTHER,
    val color: String? = null,              // Hex color code
    val tags: String = "",                   // Comma-separated tags

    // Goal Status
    val isCompleted: Boolean = false,
    val completedDate: LocalDate? = null,
    val isArchived: Boolean = false,
    val priority: GoalPriority = GoalPriority.MEDIUM,

    // Savings Strategy
    val savingStrategy: SavingStrategy = SavingStrategy.FIXED,
    val savingFrequency: SavingFrequency = SavingFrequency.DAILY,
    val autoSaveEnabled: Boolean = false,
    val autoSaveAmount: Double? = null,

    // Reminders & Notifications
    val reminderEnabled: Boolean = true,
    val reminderTime: String? = null,       // ISO time format
    val customReminderMessage: String? = null,

    // Product Information
    val productLink: String? = null,
    val productSource: String? = null,
    val lastPriceCheck: Long? = null,
    val priceDropAlertEnabled: Boolean = false,

    // Social Features
    val isShared: Boolean = false,
    val sharedWith: String = "",            // Comma-separated user IDs
    val visibilityLevel: VisibilityLevel = VisibilityLevel.PRIVATE,

    // Analytics & Tracking
    val totalDeposits: Int = 0,
    val totalWithdrawals: Int = 0,
    val longestStreak: Int = 0,
    val currentStreak: Int = 0,
    val lastSaveDate: LocalDate? = null,

    // Milestones
    val customMilestones: String = "",      // JSON array of milestones
    val achievedMilestones: String = "",    // Comma-separated milestone IDs

    // Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val version: Int = 1                     // For future migrations
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

    // Calculate days elapsed
    val daysElapsed: Long
        get() = java.time.temporal.ChronoUnit.DAYS.between(startDate, LocalDate.now())

    // Calculate expected progress based on elapsed time
    val expectedProgress: Float
        get() {
            if (totalDays <= 0) return 0f
            val elapsed = daysElapsed.toFloat()
            val total = totalDays.toFloat()
            return (elapsed / total * 100).coerceIn(0f, 100f)
        }

    // Determine if goal is on track
    val isOnTrack: Boolean
        get() = progressPercentage >= expectedProgress - 5 // 5% tolerance

    // Calculate velocity (ahead/behind schedule)
    val velocity: Float
        get() = progressPercentage - expectedProgress

    // Calculate daily saving needed (remaining amount / remaining days)
    val dailySavingNeeded: Double
        get() {
            val remainingDays = daysUntilTarget
            return if (remainingDays > 0 && remainingAmount > 0) {
                remainingAmount / remainingDays
            } else 0.0
        }

    // Calculate weekly saving needed
    val weeklySavingNeeded: Double
        get() = dailySavingNeeded * 7

    // Calculate monthly saving needed
    val monthlySavingNeeded: Double
        get() = dailySavingNeeded * 30

    // Check if goal is overdue
    val isOverdue: Boolean
        get() = LocalDate.now().isAfter(targetDate) && !isCompleted

    // Calculate completion prediction date based on current savings rate
    fun calculatePredictedCompletionDate(averageDailySaving: Double): LocalDate {
        if (averageDailySaving <= 0 || remainingAmount <= 0) {
            return targetDate
        }
        val daysNeeded = (remainingAmount / averageDailySaving).toLong()
        return LocalDate.now().plusDays(daysNeeded)
    }

    // Calculate percentage of time elapsed
    val timeElapsedPercentage: Float
        get() {
            if (totalDays <= 0) return 0f
            return (daysElapsed.toFloat() / totalDays.toFloat() * 100).coerceIn(0f, 100f)
        }

    // Get status indicator
    val status: GoalStatus
        get() = when {
            isCompleted -> GoalStatus.COMPLETED
            isOverdue -> GoalStatus.OVERDUE
            velocity > 10 -> GoalStatus.AHEAD
            velocity < -10 -> GoalStatus.BEHIND
            else -> GoalStatus.ON_TRACK
        }

    // Get next milestone
    fun getNextMilestone(): Int {
        val currentProgress = progressPercentage.toInt()
        return when {
            currentProgress < 25 -> 25
            currentProgress < 50 -> 50
            currentProgress < 75 -> 75
            currentProgress < 100 -> 100
            else -> 100
        }
    }

    // Calculate how much to save to reach next milestone
    fun amountToNextMilestone(): Double {
        val nextMilestone = getNextMilestone()
        val nextMilestoneAmount = targetAmount * (nextMilestone / 100.0)
        return (nextMilestoneAmount - currentAmount).coerceAtLeast(0.0)
    }
}

// Enums for new fields
enum class GoalCategory(val displayName: String, val icon: String) {
    ELECTRONICS("Electronics", "📱"),
    TRAVEL("Travel", "✈️"),
    EDUCATION("Education", "📚"),
    HEALTH("Health & Fitness", "💪"),
    EMERGENCY_FUND("Emergency Fund", "🚨"),
    INVESTMENT("Investment", "📈"),
    GIFT("Gift", "🎁"),
    HOME("Home & Living", "🏠"),
    VEHICLE("Vehicle", "🚗"),
    WEDDING("Wedding", "💒"),
    BUSINESS("Business", "💼"),
    ENTERTAINMENT("Entertainment", "🎮"),
    FASHION("Fashion", "👗"),
    FOOD("Food & Dining", "🍽️"),
    PET("Pet Care", "🐾"),
    OTHER("Other", "💰")
}

enum class GoalPriority(val level: Int) {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    URGENT(4)
}

enum class SavingStrategy {
    FIXED,          // Same amount daily/weekly
    FLEXIBLE,       // Adjust based on spending
    ACCELERATED,    // Increase weekly (e.g., week 1: $1, week 2: $2)
    SMART,          // AI-optimized based on patterns
    ROUND_UP,       // Round up purchases
    PERCENTAGE      // Fixed percentage of income
}

enum class SavingFrequency {
    DAILY,
    WEEKLY,
    BI_WEEKLY,
    MONTHLY,
    FLEXIBLE        // Save whenever possible
}

enum class VisibilityLevel {
    PRIVATE,        // Only you
    FRIENDS,        // Selected friends
    COMMUNITY       // Public (anonymous)
}

enum class GoalStatus(val displayName: String, val color: String) {
    ON_TRACK("On Track", "#4CAF50"),
    AHEAD("Ahead of Schedule", "#2196F3"),
    BEHIND("Behind Schedule", "#FF9800"),
    OVERDUE("Overdue", "#F44336"),
    COMPLETED("Completed", "#9C27B0")
}

// Data class for custom milestones
data class CustomMilestone(
    val id: String,
    val percentage: Int,
    val title: String,
    val description: String,
    val rewardMessage: String,
    val achieved: Boolean = false,
    val achievedDate: LocalDate? = null
)

// Extension functions for easy tag management
fun EnhancedSavingGoal.getTagsList(): List<String> {
    return tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}

fun EnhancedSavingGoal.addTag(tag: String): EnhancedSavingGoal {
    val currentTags = getTagsList().toMutableList()
    if (!currentTags.contains(tag)) {
        currentTags.add(tag)
    }
    return this.copy(tags = currentTags.joinToString(","))
}

fun EnhancedSavingGoal.removeTag(tag: String): EnhancedSavingGoal {
    val currentTags = getTagsList().toMutableList()
    currentTags.remove(tag)
    return this.copy(tags = currentTags.joinToString(","))
}