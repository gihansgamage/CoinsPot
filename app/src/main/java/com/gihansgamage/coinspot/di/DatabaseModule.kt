package com.gihansgamage.coinspot.di

import android.content.Context
import androidx.room.Room
import com.gihansgamage.coinspot.data.local.database.AppDatabase
import com.gihansgamage.coinspot.data.local.database.dao.BadgeDao
import com.gihansgamage.coinspot.data.local.database.dao.DailySavingDao
import com.gihansgamage.coinspot.data.local.database.dao.SavingGoalDao
import com.gihansgamage.coinspot.data.local.database.dao.UserProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "coinspot_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(database: AppDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    @Singleton
    fun provideSavingGoalDao(database: AppDatabase): SavingGoalDao {
        return database.savingGoalDao()
    }

    @Provides
    @Singleton
    fun provideDailySavingDao(database: AppDatabase): DailySavingDao {
        return database.dailySavingDao()
    }

    @Provides
    @Singleton
    fun provideBadgeDao(database: AppDatabase): BadgeDao {
        return database.badgeDao()
    }
}