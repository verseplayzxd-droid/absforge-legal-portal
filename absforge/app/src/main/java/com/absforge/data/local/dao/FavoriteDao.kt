package com.absforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.absforge.data.local.entity.FavoriteExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(f: FavoriteExerciseEntity)

    @Query("DELETE FROM favorite_exercises WHERE exerciseId = :exerciseId")
    fun remove(exerciseId: Int)

    @Query("SELECT * FROM favorite_exercises ORDER BY addedAt DESC")
    fun getAll(): Flow<List<FavoriteExerciseEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_exercises WHERE exerciseId = :exerciseId)")
    fun isFavorite(exerciseId: Int): Flow<Boolean>
}
