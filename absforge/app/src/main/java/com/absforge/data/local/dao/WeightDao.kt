package com.absforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.absforge.data.local.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: WeightEntryEntity)

    @Query("SELECT * FROM weight_entries ORDER BY date DESC")
    fun getAll(): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entries WHERE date >= :start AND date <= :end ORDER BY date DESC")
    fun getInRange(start: Long, end: Long): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entries ORDER BY date DESC LIMIT 1")
    fun getLatest(): Flow<WeightEntryEntity?>

    @Query("DELETE FROM weight_entries")
    suspend fun deleteAll()
}
