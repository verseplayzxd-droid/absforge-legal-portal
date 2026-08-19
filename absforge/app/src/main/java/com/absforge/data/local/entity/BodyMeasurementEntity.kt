package com.absforge.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "body_measurements")
data class BodyMeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val waistCm: Float?,
    val chestCm: Float?,
    val bodyFatPercent: Float?,
    val date: Long
)
