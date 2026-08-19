package com.absforge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val gender: String,
    val age: Int,
    val heightCm: Float,
    val weightKg: Float,
    val targetWeightKg: Float,
    val useMetric: Boolean,
    val goal: String,
    val fitnessLevel: String,
    val preferredTrainTime: String,
    val createdAt: Long
)
