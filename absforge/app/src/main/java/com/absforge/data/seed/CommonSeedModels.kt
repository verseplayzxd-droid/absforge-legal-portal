package com.absforge.data.seed

data class DayData(
    val dayNumber: Int,
    val name: String,
    val isRestDay: Boolean,
    val estimatedMinutes: Int,
    val estimatedCalories: Int,
    val exercises: List<DayExerciseData>
)

data class DayExerciseData(
    val exerciseId: Int,
    val orderIndex: Int,
    val reps: Int? = null,
    val durationSeconds: Int? = null,
    val restAfterSeconds: Int = 30
)
