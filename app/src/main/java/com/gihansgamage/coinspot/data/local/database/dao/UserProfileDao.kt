package com.gihansgamage.coinspot.data.local.database.dao

import androidx.room.*
import com.gihansgamage.coinspot.data.local.database.entities.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getUserProfileSync(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    @Update
    suspend fun updateUserProfile(profile: UserProfile)

    @Query("UPDATE user_profile SET monthlyIncome = :income, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateMonthlyIncome(income: Double, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE user_profile SET monthlyExpenses = :expenses, updatedAt = :timestamp WHERE id = 1")
    suspend fun updateMonthlyExpenses(expenses: Double, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM user_profile")
    suspend fun deleteUserProfile()
}