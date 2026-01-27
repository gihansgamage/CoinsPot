package com.gihansgamage.coinspot.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gihansgamage.coinspot.MainActivity
import com.gihansgamage.coinspot.R
import com.gihansgamage.coinspot.domain.repository.SavingGoalRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val savingGoalRepository: SavingGoalRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val CHANNEL_ID = "savings_reminders"
        private const val CHANNEL_NAME = "Savings Reminders"
        private const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        return try {
            // Create notification channel
            createNotificationChannel()

            // Get active goals
            val activeGoals = savingGoalRepository.getActiveGoals().first()

            if (activeGoals.isNotEmpty()) {
                // Send reminder notification
                sendReminderNotification(activeGoals.size)
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders for your savings goals"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendReminderNotification(goalCount: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Make sure this icon exists
            .setContentTitle("Time to Save!")
            .setContentText("You have $goalCount active savings goal${if (goalCount > 1) "s" else ""}. Add to your savings today!")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}