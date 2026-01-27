package com.gihansgamage.coinspot.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "daily_saving")
data class DailySaving(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val goalId: Int,
    val date: Date,
    val amount: Double,
    val note: String? = null,
    val isPartial: Boolean = false,
    val isSkipped: Boolean = false,
    val createdAt: Date = Date()
)