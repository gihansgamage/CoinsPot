package com.gihansgamage.coinspot.domain.repository

import com.gihansgamage.coinspot.data.local.database.dao.SavingGoalDao
import com.gihansgamage.coinspot.data.local.database.entities.SavingGoal
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val savingGoalDao: SavingGoalDao
) {
    fun getActiveGoals(): Flow<List<SavingGoal>> {
        return savingGoalDao.getActiveGoals()
    }

    suspend fun getActiveGoalsSync(): List<SavingGoal> {
        // For synchronous access (e.g., in WorkManager)
        return emptyList() // You'll need to add a suspend function in DAO for this
    }

    fun getCompletedGoals(): Flow<List<SavingGoal>> {
        return savingGoalDao.getCompletedGoals()
    }

    fun getCompletedGoalsCount(): Flow<Int> {
        return savingGoalDao.getCompletedGoalsCount()
    }

    fun getTotalSavedAmount(): Flow<Double?> {
        return savingGoalDao.getTotalSavedAmount()
    }

    suspend fun getGoalById(goalId: Int): SavingGoal? {
        return savingGoalDao.getGoalById(goalId)
    }

    suspend fun createGoal(goal: SavingGoal): Long {
        return savingGoalDao.insert(goal)
    }

    suspend fun updateGoal(goal: SavingGoal) {
        savingGoalDao.update(goal)
    }

    suspend fun deleteGoal(goal: SavingGoal) {
        savingGoalDao.delete(goal)
    }

    suspend fun addToCurrentSaved(goalId: Int, amount: Double) {
        savingGoalDao.addToCurrentSaved(goalId, amount)
        val goal = savingGoalDao.getGoalById(goalId)
        goal?.let {
            savingGoalDao.updateProgress(goalId, it.currentSaved)
        }
    }
}