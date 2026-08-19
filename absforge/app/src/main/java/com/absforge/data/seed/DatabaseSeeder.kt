package com.absforge.data.seed

import com.absforge.data.local.AbsForgeDatabase
import com.absforge.data.local.entity.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DatabaseSeeder(private val database: AbsForgeDatabase) {

    companion object {
        // Global mutex to prevent concurrent seeding from Application.onCreate()
        // and OnboardingViewModel.saveProfile() racing each other
        private val seedMutex = Mutex()
    }

    suspend fun seedIfNeeded() {
        seedMutex.withLock {
            val exerciseCount = database.exerciseDao().getCount()
            val planCount = database.workoutDao().getPlanCount()

            // 1. If exercises and plans are already present, return early safely
            if (exerciseCount >= 33 && planCount >= 3) {
                return
            }

            // 2. Ensure all 33 exercises are seeded
            if (exerciseCount < 33) {
                database.exerciseDao().insertAll(ExerciseSeedData.exercises)
            }

            // 3. Ensure all 3 plans (Beginner, Intermediate, Advanced) and 90 days are seeded
            if (planCount < 3) {
                seedPlan(1, "Beginner", "beginner", BeginnerPlanSeedData.createDays())
                seedPlan(2, "Intermediate", "intermediate", IntermediatePlanSeedData.createDays())
                seedPlan(3, "Advanced", "advanced", AdvancedPlanSeedData.createDays())
            }

            // 4. Ensure achievements are seeded
            if (database.achievementDao().getCount() < 10) {
                database.achievementDao().insertAll(AchievementSeedData.achievements)
            }
        }
    }

    private suspend fun seedPlan(planId: Int, name: String, difficulty: String, days: List<DayData>) {
        val plan = WorkoutPlanEntity(
            id = planId,
            name = name,
            difficulty = difficulty,
            totalDays = 30,
            description = "$name 30-day abs program"
        )
        database.workoutDao().insertPlan(plan)

        for (day in days) {
            val dayDeterministicId = planId * 100 + day.dayNumber
            val dayEntity = WorkoutDayEntity(
                id = dayDeterministicId,
                planId = planId,
                dayNumber = day.dayNumber,
                name = day.name,
                isRestDay = day.isRestDay,
                estimatedMinutes = day.estimatedMinutes,
                estimatedCalories = day.estimatedCalories
            )
            database.workoutDao().insertDay(dayEntity)

            val exercises = day.exercises.map { ex ->
                WorkoutDayExerciseEntity(
                    dayId = dayDeterministicId,
                    exerciseId = ex.exerciseId,
                    orderIndex = ex.orderIndex,
                    reps = ex.reps,
                    durationSeconds = ex.durationSeconds,
                    restAfterSeconds = ex.restAfterSeconds
                )
            }
            if (exercises.isNotEmpty()) {
                database.workoutDao().insertDayExercises(exercises)
            }
        }
    }
}
