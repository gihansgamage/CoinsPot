package com.gihansgamage.coinspot

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide configurations
        setupNotifications()
    }

    private fun setupNotifications() {
        // Setup periodic work for notifications
        val workRequest = androidx.work.PeriodicWorkRequestBuilder<com.gihansgamage.coinspot.workers.NotificationWorker>(
            24, // repeat interval
            java.util.concurrent.TimeUnit.HOURS
        )
            .setInitialDelay(1, java.util.concurrent.TimeUnit.HOURS)
            .build()

        androidx.work.WorkManager.getInstance(this)
            .enqueue(workRequest)
    }
}