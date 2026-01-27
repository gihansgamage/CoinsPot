package com.gihansgamage.coinspot.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gihansgamage.coinspot.R
import com.gihansgamage.coinspot.domain.repository.GoalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotificationWorker @Inject constructor(
    context: Context,
    params: WorkerParameters,
    private val goalRepository: GoalRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                // Get active goals
                val goals = goalRepository.getActiveGoalsSync()

                // Check for milestones
                goals.forEach { goal ->
                    checkAndNotifyMilestone(goal)
                }

                // Send daily reminder
                sendDailyReminder()

                Result.success()
            } catch (e: Exception) {
                Result.retry()
            }
        }
    }

    private fun checkAndNotifyMilestone(goal: com.gihansgamage.coinspot.data.local.database.entities.SavingGoal) {
        val progress = goal.progressPercentage

        val milestones = listOf(25.0, 50.0, 75.0)
        milestones.forEach { milestone ->
            if (progress >= milestone && progress < milestone + 5) {
                // Send milestone notification
                sendNotification(
                    "Milestone Reached!",
                    "You've reached ${milestone.toInt()}% of your ${goal.name} goal!"
                )
            }
        }
    }

    private fun sendDailyReminder() {
        sendNotification(
            "Daily Saving Reminder",
            "Don't forget to log your savings for today!"
        )
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create notification channel for Android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "coinspot_channel",
                "CoinsPot Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily saving reminders and milestones"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "coinspot_channel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}