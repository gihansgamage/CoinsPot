package com.gihansgamage.coinspot.data.local.database.dao

import androidx.room.*
import com.gihansgamage.coinspot.data.local.database.entities.Badge
import com.gihansgamage.coinspot.data.local.database.entities.BadgeCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {

    @Query("SELECT * FROM badges ORDER BY earnedDate DESC")
    fun getAllBadges(): Flow<List<Badge>>

    @Query("SELECT * FROM badges WHERE category = :category ORDER BY earnedDate DESC")
    fun getBadgesByCategory(category: BadgeCategory): Flow<List<Badge>>

    @Query("SELECT * FROM badges WHERE id = :badgeId")
    fun getBadgeById(badgeId: Int): Flow<Badge?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: Badge): Long

    @Delete
    suspend fun deleteBadge(badge: Badge)

    @Query("SELECT COUNT(*) FROM badges")
    fun getBadgesCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM badges WHERE category = :category")
    fun getBadgesCountByCategory(category: BadgeCategory): Flow<Int>
}