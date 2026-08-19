package com.absforge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercise_replacements")
data class ExerciseReplacementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originalExerciseId: Int,
    val replacementExerciseId: Int,
    val isPermanent: Boolean = false
)
