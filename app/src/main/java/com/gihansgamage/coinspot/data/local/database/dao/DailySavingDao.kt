package com.gihansgamage.coinspot.data.local.database.dao

import androidx.room.*
import com.gihansgamage.coinspot.data.local.database.entities.DailySaving
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface DailySavingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dailySaving: DailySaving): Long

    @Update
    suspend fun update(dailySaving: DailySaving)

    @Delete
    suspend fun delete(dailySaving: DailySaving)

    @Query("SELECT * FROM daily_saving WHERE goalId = :goalId ORDER BY date DESC")
    fun getSavingsByGoal(goalId: Int): Flow<List<DailySaving>>

    @Query("SELECT * FROM daily_saving WHERE date = :date")
    suspend fun getSavingsByDate(date: Date): List<DailySaving>

    @Query("SELECT * FROM daily_saving WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getSavingsBetweenDates(startDate: Date, endDate: Date): Flow<List<DailySaving>>

    @Query("SELECT * FROM daily_saving WHERE goalId = :goalId AND date = :date")
    suspend fun getSavingByGoalAndDate(goalId: Int, date: Date): DailySaving?

    @Query("SELECT SUM(amount) FROM daily_saving WHERE goalId = :goalId")
    suspend fun getTotalSavedForGoal(goalId: Int): Double?

    @Query("SELECT SUM(amount) FROM daily_saving WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getTotalSavedBetweenDates(startDate: Date, endDate: Date): Double?

    @Query("SELECT * FROM daily_saving WHERE date = :today")
    fun getTodaySavings(today: Date): Flow<List<DailySaving>>

    @Query("SELECT COUNT(*) FROM daily_saving WHERE goalId = :goalId AND isSkipped = 0")
    suspend fun getConsecutiveDaysForGoal(goalId: Int): Int

    @Query("DELETE FROM daily_saving WHERE goalId = :goalId")
    suspend fun deleteAllForGoal(goalId: Int)
}