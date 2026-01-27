package com.gihansgamage.coinspot.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "saving_goal")
data class SavingGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val targetPrice: Double,
    val currentSaved: Double = 0.0,
    val dailySavingAmount: Double,
    val createdAt: Date = Date(),
    val expectedEndDate: Date? = null,
    val actualEndDate: Date? = null,
    val deadline: Date? = null,
    val imageUri: String? = null,
    val currency: String,
    val progressPercentage: Double = 0.0,
    val isActive: Boolean = true,
    val isCompleted: Boolean = false,
    val daysRemaining: Int? = null,
    val daysElapsed: Int = 0
)