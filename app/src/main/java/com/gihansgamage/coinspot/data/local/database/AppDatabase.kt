package com.gihansgamage.coinspot.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gihansgamage.coinspot.data.local.database.converters.DateConverter
import com.gihansgamage.coinspot.data.local.database.converters.EnumConverters
import com.gihansgamage.coinspot.data.local.database.dao.BadgeDao
import com.gihansgamage.coinspot.data.local.database.dao.DailySavingDao
import com.gihansgamage.coinspot.data.local.database.dao.SavingGoalDao
import com.gihansgamage.coinspot.data.local.database.dao.UserProfileDao
import com.gihansgamage.coinspot.data.local.database.entities.Badge
import com.gihansgamage.coinspot.data.local.database.entities.DailySaving
import com.gihansgamage.coinspot.data.local.database.entities.SavingGoal
import com.gihansgamage.coinspot.data.local.database.entities.UserProfile

@Database(
    entities = [
        UserProfile::class,
        SavingGoal::class,
        DailySaving::class,
        Badge::class
    ],
    version = 1,
    exportSchema = false  // Set to false to avoid schema export warning
)
@TypeConverters(DateConverter::class, EnumConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun savingGoalDao(): SavingGoalDao
    abstract fun dailySavingDao(): DailySavingDao
    abstract fun badgeDao(): BadgeDao
}