package com.absforge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_workout_state")
data class ActiveWorkoutStateEntity(
    @PrimaryKey val id: Int = 1,
    val sessionId: Int,
    val currentExerciseIndex: Int,
    val currentPhase: String,
    val remainingTimeMs: Long,
    val isPaused: Boolean,
    val savedAt: Long
)
