package com.gihansgamage.coinspot.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val description: String,
    val iconName: String,
    val earnedDate: LocalDate,
    val category: BadgeCategory
)

enum class BadgeCategory {
    FIRST_GOAL,
    GOAL_ACHIEVED,
    STREAK,
    AMOUNT_MILESTONE,
    CONSISTENCY
}