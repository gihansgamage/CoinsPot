package com.gihansgamage.coinspot.data.local.database.dao

import androidx.room.*
import com.gihansgamage.coinspot.data.local.database.entities.SavingGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingGoalDao {

    @Query("SELECT * FROM saving_goals ORDER BY createdAt DESC")
    fun getAllGoals(): Flow<List<SavingGoal>>

    @Query("SELECT * FROM saving_goals WHERE isCompleted = 0 ORDER BY targetDate ASC")
    fun getActiveGoals(): Flow<List<SavingGoal>>

    @Query("SELECT * FROM saving_goals WHERE isCompleted = 1 ORDER BY completedDate DESC")
    fun getCompletedGoals(): Flow<List<SavingGoal>>

    @Query("SELECT * FROM saving_goals WHERE id = :goalId")
    fun getGoalById(goalId: Int): Flow<SavingGoal?>

    @Query("SELECT * FROM saving_goals WHERE id = :goalId")
    suspend fun getGoalByIdSync(goalId: Int): SavingGoal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: SavingGoal): Long

    @Update
    suspend fun updateGoal(goal: SavingGoal)

    @Delete
    suspend fun deleteGoal(goal: SavingGoal)

    @Query("DELETE FROM saving_goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Int)

    @Query("UPDATE saving_goals SET currentAmount = currentAmount + :amount WHERE id = :goalId")
    suspend fun addToGoal(goalId: Int, amount: Double)

    @Query("UPDATE saving_goals SET currentAmount = currentAmount - :amount WHERE id = :goalId")
    suspend fun withdrawFromGoal(goalId: Int, amount: Double)

    @Query("UPDATE saving_goals SET currentAmount = :newAmount WHERE id = :goalId")
    suspend fun updateCurrentAmount(goalId: Int, newAmount: Double)

    @Query("UPDATE saving_goals SET targetDate = :newDate WHERE id = :goalId")
    suspend fun updateTargetDate(goalId: Int, newDate: java.time.LocalDate)

    @Query("UPDATE saving_goals SET isCompleted = 1, completedDate = :completedDate WHERE id = :goalId")
    suspend fun markAsCompleted(goalId: Int, completedDate: java.time.LocalDate)

    @Query("SELECT COUNT(*) FROM saving_goals WHERE isCompleted = 1")
    fun getCompletedGoalsCount(): Flow<Int>

    @Query("SELECT SUM(currentAmount) FROM saving_goals WHERE isCompleted = 0")
    fun getTotalSavings(): Flow<Double?>

    @Query("SELECT SUM(targetAmount - currentAmount) FROM saving_goals WHERE isCompleted = 0")
    fun getTotalRemainingAmount(): Flow<Double?>
}