package com.absforge.data.seed

import com.absforge.data.local.entity.AchievementEntity

object AchievementSeedData {
    val achievements = listOf(
        AchievementEntity("first_workout", "First Step", "Complete your first workout", "ic_ach_first", null, false),
        AchievementEntity("streak_3", "On a Roll", "Achieve a 3-day streak", "ic_ach_streak_3", null, false),
        AchievementEntity("streak_7", "Unstoppable", "Achieve a 7-day streak", "ic_ach_streak_7", null, false),
        AchievementEntity("streak_14", "Core Committed", "Achieve a 14-day streak", "ic_ach_streak_14", null, false),
        AchievementEntity("streak_30", "Iron Will", "Achieve a 30-day streak", "ic_ach_streak_30", null, false),
        AchievementEntity("workouts_5", "Getting Started", "Complete 5 workouts", "ic_ach_work_5", null, false),
        AchievementEntity("workouts_10", "Regular", "Complete 10 workouts", "ic_ach_work_10", null, false),
        AchievementEntity("workouts_25", "Veteran", "Complete 25 workouts", "ic_ach_work_25", null, false),
        AchievementEntity("minutes_100", "Time Invested", "Workout for 100 minutes", "ic_ach_min_100", null, false),
        AchievementEntity("calories_500", "Burner", "Burn 500 calories", "ic_ach_cal_500", null, false),
        AchievementEntity("calories_1000", "Incinerator", "Burn 1000 calories", "ic_ach_cal_1000", null, false)
    )
}
