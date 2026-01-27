package com.gihansgamage.coinspot.domain.repository

import com.gihansgamage.coinspot.data.local.database.dao.UserProfileDao
import com.gihansgamage.coinspot.data.local.database.entities.UserProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userProfileDao: UserProfileDao
) {

    fun getUserProfile(): Flow<UserProfile?> = userProfileDao.getUserProfile()

    suspend fun createUserProfile(profile: UserProfile) {
        userProfileDao.insertUserProfile(profile)
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        userProfileDao.updateUserProfile(profile)
    }

    suspend fun updateMonthlyIncome(income: Double) {
        userProfileDao.updateMonthlyIncome(income)
    }

    suspend fun updateMonthlyExpenses(expenses: Double) {
        userProfileDao.updateMonthlyExpenses(expenses)
    }

    suspend fun deleteUserProfile() {
        userProfileDao.deleteUserProfile()
    }
}