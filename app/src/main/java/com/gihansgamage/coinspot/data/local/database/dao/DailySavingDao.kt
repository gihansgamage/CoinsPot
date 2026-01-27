package com.gihansgamage.coinspot.data.local.database.dao

import androidx.room.*
import com.gihansgamage.coinspot.data.local.database.entities.DailySaving
import com.gihansgamage.coinspot.data.local.database.entities.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface DailySavingDao {

    @Query("SELECT * FROM daily_savings ORDER BY date DESC, createdAt DESC")
    fun getAllSavings(): Flow<List<DailySaving>>

    @Query("SELECT * FROM daily_savings WHERE goalId = :goalId ORDER BY date DESC")
    fun getSavingsByGoal(goalId: Int): Flow<List<DailySaving>>

    @Query("SELECT * FROM daily_savings WHERE date = :date ORDER BY createdAt DESC")
    fun getSavingsByDate(date: LocalDate): Flow<List<DailySaving>>

    @Query("SELECT * FROM daily_savings WHERE goalId = :goalId AND date = :date")
    fun getSavingsByGoalAndDate(goalId: Int, date: LocalDate): Flow<List<DailySaving>>

    @Query("SELECT * FROM daily_savings WHERE goalId = :goalId AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    fun getSavingsByDateRange(goalId: Int, startDate: LocalDate, endDate: LocalDate): Flow<List<DailySaving>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSaving(saving: DailySaving): Long

    @Update
    suspend fun updateSaving(saving: DailySaving)

    @Delete
    suspend fun deleteSaving(saving: DailySaving)

    @Query("DELETE FROM daily_savings WHERE id = :savingId")
    suspend fun deleteSavingById(savingId: Int)

    @Query("SELECT SUM(CASE WHEN transactionType = 'DEPOSIT' THEN amount ELSE -amount END) FROM daily_savings WHERE goalId = :goalId")
    fun getTotalSavedForGoal(goalId: Int): Flow<Double?>

    @Query("SELECT SUM(amount) FROM daily_savings WHERE goalId = :goalId AND transactionType = :type")
    fun getTotalByType(goalId: Int, type: TransactionType): Flow<Double?>

    @Query("SELECT AVG(amount) FROM daily_savings WHERE goalId = :goalId AND transactionType = 'DEPOSIT'")
    fun getAverageDailySaving(goalId: Int): Flow<Double?>

    @Query("SELECT COUNT(DISTINCT date) FROM daily_savings WHERE goalId = :goalId AND transactionType = 'DEPOSIT'")
    fun getSavingStreak(goalId: Int): Flow<Int>

    @Query("SELECT * FROM daily_savings WHERE goalId = :goalId ORDER BY date DESC LIMIT 7")
    fun getLastWeekSavings(goalId: Int): Flow<List<DailySaving>>

    @Query("SELECT COUNT(*) FROM daily_savings WHERE date = :date")
    suspend fun hasSavingsOnDate(date: LocalDate): Int

    @Query("SELECT date, SUM(CASE WHEN transactionType = 'DEPOSIT' THEN amount ELSE -amount END) as total FROM daily_savings WHERE goalId = :goalId GROUP BY date ORDER BY date DESC LIMIT :limit")
    fun getDailySavingsHistory(goalId: Int, limit: Int = 30): Flow<List<DailySavingHistory>>
}

data class DailySavingHistory(
    val date: LocalDate,
    val total: Double
)