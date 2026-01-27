package com.gihansgamage.coinspot.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gihansgamage.coinspot.data.local.database.converters.DateConverter
import com.gihansgamage.coinspot.data.local.database.dao.*
import com.gihansgamage.coinspot.data.local.database.entities.*

@Database(
    entities = [
        UserProfile::class,
        SavingGoal::class,
        DailySaving::class,
        Badge::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun savingGoalDao(): SavingGoalDao
    abstract fun dailySavingDao(): DailySavingDao
    abstract fun badgeDao(): BadgeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "coinspot_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}