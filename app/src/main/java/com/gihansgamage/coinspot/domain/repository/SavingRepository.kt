package com.gihansgamage.coinspot.domain.repository

import com.gihansgamage.coinspot.data.local.database.dao.DailySavingDao
import com.gihansgamage.coinspot.data.local.database.entities.DailySaving
import kotlinx.coroutines.flow.Flow
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavingRepository @Inject constructor(
    private val dailySavingDao: DailySavingDao
) {
    fun getTodaySavings(): Flow<List<DailySaving>> {
        val today = Date()
        return dailySavingDao.getTodaySavings(today)
    }

    fun getSavingsByGoal(goalId: Int): Flow<List<DailySaving>> {
        return dailySavingDao.getSavingsByGoal(goalId)
    }

    suspend fun getSavingsByDate(date: Date): List<DailySaving> {
        return dailySavingDao.getSavingsByDate(date)
    }

    fun getSavingsBetweenDates(startDate: Date, endDate: Date): Flow<List<DailySaving>> {
        return dailySavingDao.getSavingsBetweenDates(startDate, endDate)
    }

    suspend fun addSaving(dailySaving: DailySaving): Long {
        return dailySavingDao.insert(dailySaving)
    }

    suspend fun updateSaving(dailySaving: DailySaving) {
        dailySavingDao.update(dailySaving)
    }

    suspend fun deleteSaving(dailySaving: DailySaving) {
        dailySavingDao.delete(dailySaving)
    }

    suspend fun getTotalSavedForGoal(goalId: Int): Double {
        return dailySavingDao.getTotalSavedForGoal(goalId) ?: 0.0
    }

    suspend fun getTotalSavedBetweenDates(startDate: Date, endDate: Date): Double {
        return dailySavingDao.getTotalSavedBetweenDates(startDate, endDate) ?: 0.0
    }
}