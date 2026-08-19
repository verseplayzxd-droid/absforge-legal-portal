package com.absforge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val category: String,
    val difficulty: String,
    val targetMuscle: String,
    val instructions: String,
    val tips: String,
    val commonMistakes: String,
    val animationId: String,
    val repType: String,
    val defaultReps: Int,
    val defaultDurationSeconds: Int,
    val estimatedCaloriesPerMinute: Float,
    val isSideSpecific: Boolean,
    val equipment: String = "none",
    val alternativeExerciseIds: String
)
