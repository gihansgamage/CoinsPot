package com.gihansgamage.coinspot.data.local.database.dao

import androidx.room.*
import com.gihansgamage.coinspot.data.local.database.entities.SavingGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingGoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: SavingGoal): Long

    @Update
    suspend fun update(goal: SavingGoal)

    @Delete
    suspend fun delete(goal: SavingGoal)

    @Query("SELECT * FROM saving_goal WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveGoals(): Flow<List<SavingGoal>>

    @Query("SELECT * FROM saving_goal WHERE id = :goalId")
    suspend fun getGoalById(goalId: Int): SavingGoal?

    @Query("SELECT * FROM saving_goal WHERE isCompleted = 1 ORDER BY actualEndDate DESC")
    fun getCompletedGoals(): Flow<List<SavingGoal>>

    @Query("UPDATE saving_goal SET currentSaved = currentSaved + :amount WHERE id = :goalId")
    suspend fun addToCurrentSaved(goalId: Int, amount: Double)

    @Query("UPDATE saving_goal SET progressPercentage = (:currentSaved / targetPrice) * 100 WHERE id = :goalId")
    suspend fun updateProgress(goalId: Int, currentSaved: Double)

    @Query("SELECT COUNT(*) FROM saving_goal WHERE isCompleted = 1")
    fun getCompletedGoalsCount(): Flow<Int>

    @Query("SELECT SUM(currentSaved) FROM saving_goal WHERE isCompleted = 1")
    fun getTotalSavedAmount(): Flow<Double?>
}