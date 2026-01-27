package com.gihansgamage.coinspot

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        private const val TAG = "CoinsPot"
    }

    override fun onCreate() {
        super.onCreate()
        try {
            Log.d(TAG, "=== App onCreate started ===")

            // TEMPORARY: Comment out notification setup to test if it's causing the crash
            // Uncomment this after app starts successfully
//             setupNotifications()

            Log.d(TAG, "=== App onCreate completed successfully ===")
        } catch (e: Exception) {
            Log.e(TAG, "FATAL ERROR in App onCreate", e)
            e.printStackTrace()
        }
    }

    private fun setupNotifications() {
        try {
            Log.d(TAG, "Setting up notifications...")

            val workRequest = androidx.work.PeriodicWorkRequestBuilder<com.gihansgamage.coinspot.workers.NotificationWorker>(
                24, // repeat interval
                java.util.concurrent.TimeUnit.HOURS
            )
                .setInitialDelay(1, java.util.concurrent.TimeUnit.HOURS)
                .build()

            androidx.work.WorkManager.getInstance(this)
                .enqueueUniquePeriodicWork(
                    "savings_reminder",
                    androidx.work.ExistingPeriodicWorkPolicy.KEEP,
                    workRequest
                )

            Log.d(TAG, "Notifications setup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up notifications (non-fatal)", e)
            // Don't crash the app if notification setup fails
        }
    }
}