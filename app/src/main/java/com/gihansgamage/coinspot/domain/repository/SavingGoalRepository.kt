package com.gihansgamage.coinspot.domain.repository

import com.gihansgamage.coinspot.data.local.database.dao.DailySavingDao
import com.gihansgamage.coinspot.data.local.database.dao.SavingGoalDao
import com.gihansgamage.coinspot.data.local.database.entities.DailySaving
import com.gihansgamage.coinspot.data.local.database.entities.SavingGoal
import com.gihansgamage.coinspot.data.local.database.entities.TransactionType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavingGoalRepository @Inject constructor(
    private val goalDao: SavingGoalDao,
    private val dailySavingDao: DailySavingDao
) {

    fun getAllGoals(): Flow<List<SavingGoal>> = goalDao.getAllGoals()

    fun getActiveGoals(): Flow<List<SavingGoal>> = goalDao.getActiveGoals()

    fun getCompletedGoals(): Flow<List<SavingGoal>> = goalDao.getCompletedGoals()

    fun getGoalById(goalId: Int): Flow<SavingGoal?> = goalDao.getGoalById(goalId)

    fun getTotalSavings(): Flow<Double?> = goalDao.getTotalSavings()

    fun getTotalRemainingAmount(): Flow<Double?> = goalDao.getTotalRemainingAmount()

    fun getCompletedGoalsCount(): Flow<Int> = goalDao.getCompletedGoalsCount()

    suspend fun createGoal(goal: SavingGoal): Long {
        return goalDao.insertGoal(goal)
    }

    suspend fun updateGoal(goal: SavingGoal) {
        goalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: SavingGoal) {
        goalDao.deleteGoal(goal)
    }

    suspend fun addMoneyToGoal(
        goalId: Int,
        amount: Double,
        note: String = ""
    ): Result<Unit> {
        return try {
            val goal = goalDao.getGoalByIdSync(goalId)
                ?: return Result.failure(Exception("Goal not found"))

            if (amount <= 0) {
                return Result.failure(Exception("Amount must be greater than 0"))
            }

            val newAmount = (goal.currentAmount + amount).coerceAtMost(goal.targetAmount)
            goalDao.updateCurrentAmount(goalId, newAmount)

            val dailySaving = DailySaving(
                goalId = goalId,
                amount = amount,
                date = LocalDate.now(),
                note = note,
                transactionType = TransactionType.DEPOSIT
            )
            dailySavingDao.insertSaving(dailySaving)

            if (newAmount >= goal.targetAmount) {
                goalDao.markAsCompleted(goalId, LocalDate.now())
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun withdrawMoneyFromGoal(
        goalId: Int,
        amount: Double,
        note: String = ""
    ): Result<Unit> {
        return try {
            val goal = goalDao.getGoalByIdSync(goalId)
                ?: return Result.failure(Exception("Goal not found"))

            if (amount <= 0) {
                return Result.failure(Exception("Amount must be greater than 0"))
            }

            if (amount > goal.currentAmount) {
                return Result.failure(Exception("Insufficient funds. Current amount: ${goal.currencySymbol}${goal.currentAmount}"))
            }

            val newAmount = (goal.currentAmount - amount).coerceAtLeast(0.0)
            goalDao.updateCurrentAmount(goalId, newAmount)

            val dailySaving = DailySaving(
                goalId = goalId,
                amount = amount,
                date = LocalDate.now(),
                note = note,
                transactionType = TransactionType.WITHDRAWAL
            )
            dailySavingDao.insertSaving(dailySaving)

            updateTargetDate(goalId)

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateTargetDate(goalId: Int) {
        try {
            val goal = goalDao.getGoalByIdSync(goalId) ?: return
            val averageDailySaving = dailySavingDao.getAverageDailySaving(goalId).first() ?: 0.0

            if (averageDailySaving > 0 && goal.remainingAmount > 0) {
                val newTargetDate = goal.calculateNewTargetDate(averageDailySaving)
                goalDao.updateTargetDate(goalId, newTargetDate)
            }
        } catch (e: Exception) {
            // Handle error
        }
    }

    fun getSavingsHistory(goalId: Int): Flow<List<DailySaving>> {
        return dailySavingDao.getSavingsByGoal(goalId)
    }

    fun getTotalSavedForGoal(goalId: Int): Flow<Double?> {
        return dailySavingDao.getTotalSavedForGoal(goalId)
    }

    fun getAverageDailySaving(goalId: Int): Flow<Double?> {
        return dailySavingDao.getAverageDailySaving(goalId)
    }

    fun getSavingStreak(goalId: Int): Flow<Int> {
        return dailySavingDao.getSavingStreak(goalId)
    }
}