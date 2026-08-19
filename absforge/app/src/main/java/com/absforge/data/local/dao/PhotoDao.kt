package com.absforge.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.absforge.data.local.entity.ProgressPhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(p: ProgressPhotoEntity)

    @Delete
    fun delete(p: ProgressPhotoEntity)

    @Query("SELECT * FROM progress_photos ORDER BY date DESC")
    fun getAll(): Flow<List<ProgressPhotoEntity>>

    @Query("SELECT * FROM progress_photos WHERE type = :type ORDER BY date DESC")
    fun getByType(type: String): Flow<List<ProgressPhotoEntity>>
}
