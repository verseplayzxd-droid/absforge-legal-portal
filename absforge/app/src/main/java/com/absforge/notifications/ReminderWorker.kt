package com.absforge.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.absforge.AbsForgeApplication

class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val app = context.applicationContext as? AbsForgeApplication
            val dayNumber = 1 // Simplified: get from DB in a real scenario
            NotificationHelper.showWorkoutReminder(context, dayNumber)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
