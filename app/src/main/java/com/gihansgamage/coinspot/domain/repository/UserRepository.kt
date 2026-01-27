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
    suspend fun saveUserProfile(userProfile: UserProfile): Long {
        return userProfileDao.insert(userProfile)
    }

    suspend fun updateUserProfile(userProfile: UserProfile) {
        userProfileDao.update(userProfile)
    }

    suspend fun getUserProfile(): UserProfile? {
        return userProfileDao.getUserProfile()
    }

    fun getUserProfileFlow(): Flow<UserProfile?> {
        return userProfileDao.getUserProfileFlow()
    }

    suspend fun deleteUserProfile(userProfile: UserProfile) {
        userProfileDao.delete(userProfile)
    }

    suspend fun clearAllData() {
        userProfileDao.deleteAll()
    }
}