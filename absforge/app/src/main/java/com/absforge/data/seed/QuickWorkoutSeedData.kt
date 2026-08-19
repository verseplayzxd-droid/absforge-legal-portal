package com.absforge.data.seed

object QuickWorkoutSeedData {
    val workouts: Map<String, DayData> = mapOf(
        "five_min_abs" to DayData(
            dayNumber = 0,
            name = "5 Minute Abs",
            isRestDay = false,
            estimatedMinutes = 5,
            estimatedCalories = 35,
            exercises = listOf(
                DayExerciseData(1, 0, 12, null, 20),
                DayExerciseData(9, 1, 14, null, 20),
                DayExerciseData(2, 2, 10, null, 20),
                DayExerciseData(13, 3, null, 20, 20),
                DayExerciseData(30, 4, null, 15, 0)
            )
        ),
        "lower_abs" to DayData(
            dayNumber = 0,
            name = "Lower Abs Focus",
            isRestDay = false,
            estimatedMinutes = 8,
            estimatedCalories = 55,
            exercises = listOf(
                DayExerciseData(2, 0, 15, null, 25),
                DayExerciseData(6, 1, 12, null, 25),
                DayExerciseData(8, 2, null, 30, 25),
                DayExerciseData(26, 3, 14, null, 25),
                DayExerciseData(16, 4, 16, null, 25),
                DayExerciseData(31, 5, null, 30, 0)
            )
        ),
        "core_burner" to DayData(
            dayNumber = 0,
            name = "Core Burner",
            isRestDay = false,
            estimatedMinutes = 10,
            estimatedCalories = 80,
            exercises = listOf(
                DayExerciseData(12, 0, null, 30, 20),
                DayExerciseData(4, 1, 15, null, 20),
                DayExerciseData(5, 2, 12, null, 20),
                DayExerciseData(24, 3, 16, null, 20),
                DayExerciseData(29, 4, 20, null, 20),
                DayExerciseData(3, 5, 20, null, 20),
                DayExerciseData(28, 6, null, 30, 20),
                DayExerciseData(30, 7, null, 30, 0)
            )
        ),
        "plank_challenge" to DayData(
            dayNumber = 0,
            name = "Plank Challenge",
            isRestDay = false,
            estimatedMinutes = 7,
            estimatedCalories = 50,
            exercises = listOf(
                DayExerciseData(13, 0, null, 45, 30),
                DayExerciseData(14, 1, null, 30, 20),
                DayExerciseData(15, 2, null, 30, 30),
                DayExerciseData(12, 3, null, 40, 30),
                DayExerciseData(13, 4, null, 60, 0)
            )
        ),
        "oblique_blast" to DayData(
            dayNumber = 0,
            name = "Oblique Blast",
            isRestDay = false,
            estimatedMinutes = 8,
            estimatedCalories = 60,
            exercises = listOf(
                DayExerciseData(9, 0, 20, null, 20),
                DayExerciseData(10, 1, 20, null, 20),
                DayExerciseData(25, 2, 16, null, 20),
                DayExerciseData(7, 3, 14, null, 20),
                DayExerciseData(3, 4, 24, null, 20),
                DayExerciseData(32, 5, null, 30, 0)
            )
        ),
        "stretch_recovery" to DayData(
            dayNumber = 0,
            name = "Stretch & Recovery",
            isRestDay = false,
            estimatedMinutes = 6,
            estimatedCalories = 25,
            exercises = listOf(
                DayExerciseData(20, 0, 10, null, 15),
                DayExerciseData(19, 1, 10, null, 15),
                DayExerciseData(30, 2, null, 45, 10),
                DayExerciseData(31, 3, null, 45, 10),
                DayExerciseData(32, 4, null, 45, 10),
                DayExerciseData(33, 5, null, 45, 0)
            )
        )
    )
}
