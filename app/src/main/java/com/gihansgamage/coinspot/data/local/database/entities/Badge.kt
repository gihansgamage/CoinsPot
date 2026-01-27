package com.gihansgamage.coinspot.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "badge")
data class Badge(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val badgeType: String, // "first_goal", "streak_7", "streak_30", "consistent_saver"
    val title: String,
    val description: String,
    val iconRes: String,
    val achievedAt: Date = Date(),
    val isUnlocked: Boolean = false
)