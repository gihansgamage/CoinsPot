package com.gihansgamage.coinspot.data.local.database.dao

import androidx.room.*
import com.gihansgamage.coinspot.data.local.database.entities.Badge
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(badge: Badge): Long

    @Update
    suspend fun update(badge: Badge)

    @Delete
    suspend fun delete(badge: Badge)

    @Query("SELECT * FROM badge WHERE userId = :userId ORDER BY achievedAt DESC")
    fun getBadgesByUser(userId: Int): Flow<List<Badge>>

    @Query("SELECT * FROM badge WHERE userId = :userId AND isUnlocked = 1 ORDER BY achievedAt DESC")
    fun getUnlockedBadges(userId: Int): Flow<List<Badge>>

    @Query("SELECT * FROM badge WHERE userId = :userId AND isUnlocked = 0")
    fun getLockedBadges(userId: Int): Flow<List<Badge>>

    @Query("SELECT * FROM badge WHERE userId = :userId AND badgeType = :badgeType")
    suspend fun getBadgeByType(userId: Int, badgeType: String): Badge?

    @Query("UPDATE badge SET isUnlocked = 1 WHERE id = :badgeId")
    suspend fun unlockBadge(badgeId: Int)

    @Query("SELECT COUNT(*) FROM badge WHERE userId = :userId AND isUnlocked = 1")
    fun getUnlockedBadgeCount(userId: Int): Flow<Int>

    @Query("DELETE FROM badge WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: Int)
}