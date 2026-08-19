package com.absforge.data.repository

import com.absforge.data.local.AbsForgeDatabase
import com.absforge.data.local.entity.WorkoutSessionEntity
import com.absforge.data.preferences.PreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Calendar

class WorkoutRepository(
    private val db: AbsForgeDatabase,
    private val preferencesManager: PreferencesManager
) {

    fun observeCompletedSessions(): Flow<List<WorkoutSessionEntity>> {
        return db.sessionDao().getAllSessions()
    }

    fun observeAchievements() = db.achievementDao().getAll()

    suspend fun completeWorkoutSession(
        sessionId: Int,
        completedExercises: Int,
        totalExercises: Int,
        durationSeconds: Int,
        caloriesBurned: Int,
        isQuickWorkout: Boolean
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val session = db.sessionDao().getSessionById(sessionId) ?: return@withContext false
            val now = System.currentTimeMillis()

            val updatedSession = session.copy(
                endTime = now,
                completedExercises = completedExercises,
                totalExercises = totalExercises,
                durationSeconds = durationSeconds,
                caloriesBurned = caloriesBurned
            )
            db.sessionDao().updateSession(updatedSession)

            val completionRatio = completedExercises.toFloat() / totalExercises.coerceAtLeast(1)
            val isQualified = completionRatio >= 0.90f

            // If 90% rule passes and it's a main 30-day challenge workout, advance day counter
            if (isQualified && !isQuickWorkout) {
                val currentDay = preferencesManager.currentDay.first()
                if (currentDay == session.dayNumber && currentDay < 30) {
                    preferencesManager.setCurrentDay(currentDay + 1)
                }
            }

            // Re-evaluate achievements
            evaluateAchievements()

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun evaluateAchievements() {
        val sessions = db.sessionDao().getAllSessions().first()
        val completed = sessions.filter {
            (it.completedExercises.toFloat() / it.totalExercises.coerceAtLeast(1)) >= 0.90f
        }

        val totalWorkouts = completed.size
        val totalMins = completed.sumOf { it.durationSeconds } / 60
        val totalCals = completed.sumOf { it.caloriesBurned }

        // Compute streak
        var streakCount = 0
        val sortedSessions = completed.filter { it.endTime != null }
            .sortedByDescending { it.endTime ?: 0L }

        if (sortedSessions.isNotEmpty()) {
            val todayDay = System.currentTimeMillis() / 86400000L
            var currentStreakDay = (sortedSessions.first().endTime ?: 0L) / 86400000L

            if (todayDay - currentStreakDay <= 1) {
                streakCount = 1
                for (i in 1 until sortedSessions.size) {
                    val sessionDay = (sortedSessions[i].endTime ?: 0L) / 86400000L
                    if (currentStreakDay - sessionDay == 1L) {
                        streakCount++
                        currentStreakDay = sessionDay
                    } else if (currentStreakDay - sessionDay > 1L) {
                        break
                    }
                }
            }
        }

        val now = System.currentTimeMillis()

        if (totalWorkouts >= 1) db.achievementDao().unlock("first_workout", now)
        if (totalWorkouts >= 5) db.achievementDao().unlock("workouts_5", now)
        if (totalWorkouts >= 10) db.achievementDao().unlock("workouts_10", now)
        if (totalWorkouts >= 25) db.achievementDao().unlock("workouts_25", now)

        if (streakCount >= 3) db.achievementDao().unlock("streak_3", now)
        if (streakCount >= 7) db.achievementDao().unlock("streak_7", now)
        if (streakCount >= 14) db.achievementDao().unlock("streak_14", now)
        if (streakCount >= 30) db.achievementDao().unlock("streak_30", now)

        if (totalMins >= 100) db.achievementDao().unlock("minutes_100", now)
        if (totalCals >= 500) db.achievementDao().unlock("calories_500", now)
        if (totalCals >= 1000) db.achievementDao().unlock("calories_1000", now)
    }
}
