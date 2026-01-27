package com.gihansgamage.coinspot.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val age: Int,
    val monthlyIncome: Double,
    val livingCosts: Double,
    val foodExpenses: Double,
    val otherCosts: Double,
    val currency: String,
    val country: String,
    val savingStyle: String, // "conservative", "balanced", "aggressive"
    val dailySavingAmount: Double,
    val disposableIncome: Double,
    val createdAt: Date = Date(),
    val lastUpdated: Date = Date()
)