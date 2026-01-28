package com.gihansgamage.coinspot.domain.repository

import com.gihansgamage.coinspot.data.local.database.dao.BadgeDao
import com.gihansgamage.coinspot.data.local.database.entities.Badge
import com.gihansgamage.coinspot.data.local.database.entities.BadgeCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BadgeRepository @Inject constructor(
    private val badgeDao: BadgeDao
) {

    fun getAllBadges(): Flow<List<Badge>> = badgeDao.getAllBadges()

    fun getBadgesByCategory(category: BadgeCategory): Flow<List<Badge>> =
        badgeDao.getBadgesByCategory(category)

    fun getBadgeById(badgeId: Int): Flow<Badge?> = badgeDao.getBadgeById(badgeId)

    fun getBadgesCount(): Flow<Int> = badgeDao.getBadgesCount()

    fun getBadgesCountByCategory(category: BadgeCategory): Flow<Int> =
        badgeDao.getBadgesCountByCategory(category)

    suspend fun awardBadge(
        name: String,
        description: String,
        iconName: String,
        category: BadgeCategory
    ): Long {
        val badge = Badge(
            name = name,
            description = description,
            iconName = iconName,
            earnedDate = LocalDate.now(),
            category = category
        )
        return badgeDao.insertBadge(badge)
    }

    suspend fun deleteBadge(badge: Badge) {
        badgeDao.deleteBadge(badge)
    }

    suspend fun checkAndAwardBadges(
        totalGoalsCompleted: Int,
        savingStreak: Int,
        totalSaved: Double
    ) {
        // Award "First Goal" badge
        if (totalGoalsCompleted == 1) {
            awardBadge(
                name = "Goal Setter",
                description = "Created your first savings goal",
                iconName = "goal",
                category = BadgeCategory.FIRST_GOAL
            )
        }

        // Award milestone badges
        when {
            totalGoalsCompleted >= 5 -> awardBadge(
                name = "Goal Master",
                description = "Completed 5 goals",
                iconName = "goal_master",
                category = BadgeCategory.GOAL_ACHIEVED
            )
            totalGoalsCompleted >= 3 -> awardBadge(
                name = "Goal Achiever",
                description = "Completed 3 goals",
                iconName = "goal_achiever",
                category = BadgeCategory.GOAL_ACHIEVED
            )
        }

        // Award streak badges
        when {
            savingStreak >= 100 -> awardBadge(
                name = "Century Saver",
                description = "Saved for 100 consecutive days",
                iconName = "century",
                category = BadgeCategory.STREAK
            )
            savingStreak >= 30 -> awardBadge(
                name = "Monthly Warrior",
                description = "30-day saving streak",
                iconName = "monthly_warrior",
                category = BadgeCategory.STREAK
            )
            savingStreak >= 7 -> awardBadge(
                name = "Persistent Saver",
                description = "7-day saving streak",
                iconName = "persistent",
                category = BadgeCategory.STREAK
            )
        }

        // Award amount milestones
        when {
            totalSaved >= 10000 -> awardBadge(
                name = "Ten Thousand Club",
                description = "Saved 10,000+",
                iconName = "ten_thousand",
                category = BadgeCategory.AMOUNT_MILESTONE
            )
            totalSaved >= 5000 -> awardBadge(
                name = "Five Thousand Club",
                description = "Saved 5,000+",
                iconName = "five_thousand",
                category = BadgeCategory.AMOUNT_MILESTONE
            )
            totalSaved >= 1000 -> awardBadge(
                name = "Thousand Club",
                description = "Saved 1,000+",
                iconName = "thousand",
                category = BadgeCategory.AMOUNT_MILESTONE
            )
        }
    }
}