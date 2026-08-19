package com.absforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.absforge.data.local.entity.BodyMeasurementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(m: BodyMeasurementEntity)

    @Query("SELECT * FROM body_measurements ORDER BY date DESC")
    fun getAll(): Flow<List<BodyMeasurementEntity>>

    @Query("SELECT * FROM body_measurements ORDER BY date DESC LIMIT 1")
    fun getLatest(): Flow<BodyMeasurementEntity?>

    @Query("DELETE FROM body_measurements")
    fun deleteAll()
}
