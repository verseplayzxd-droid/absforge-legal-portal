package com.absforge.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_days",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("planId"), Index(value = ["planId", "dayNumber"], unique = true)]
)
data class WorkoutDayEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val planId: Int,
    val dayNumber: Int,
    val name: String,
    val isRestDay: Boolean,
    val estimatedMinutes: Int,
    val estimatedCalories: Int
)
