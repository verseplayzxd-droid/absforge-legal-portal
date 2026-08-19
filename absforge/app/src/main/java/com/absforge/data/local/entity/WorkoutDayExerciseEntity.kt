package com.absforge.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_day_exercises",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutDayEntity::class,
            parentColumns = ["id"],
            childColumns = ["dayId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("dayId"), Index("exerciseId")]
)
data class WorkoutDayExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dayId: Int,
    val exerciseId: Int,
    val orderIndex: Int,
    val reps: Int?,
    val durationSeconds: Int?,
    val restAfterSeconds: Int
)
