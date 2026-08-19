package com.absforge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val planId: Int,
    val dayNumber: Int,
    val startTime: Long,
    val endTime: Long?,
    val completedExercises: Int,
    val totalExercises: Int,
    val caloriesBurned: Int,
    val durationSeconds: Int,
    val feedback: String?,
    val isQuickWorkout: Boolean = false,
    val quickWorkoutId: String? = null
)
