package com.gihansgamage.coinspot.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1, // Single user profile
    val name: String,
    val country: String,
    val currency: String,
    val currencySymbol: String,
    val monthlyIncome: Double = 0.0,
    val monthlyExpenses: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val disposableIncome: Double
        get() = (monthlyIncome - monthlyExpenses).coerceAtLeast(0.0)
}