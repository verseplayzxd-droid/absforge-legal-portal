package com.absforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.absforge.data.local.entity.ExerciseReplacementEntity

@Dao
interface ReplacementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(r: ExerciseReplacementEntity)

    @Query("SELECT * FROM exercise_replacements WHERE originalExerciseId = :exerciseId LIMIT 1")
    fun getForExercise(exerciseId: Int): ExerciseReplacementEntity?

    @Query("SELECT * FROM exercise_replacements WHERE isPermanent = 1")
    fun getPermanentReplacements(): List<ExerciseReplacementEntity>

    @Query("DELETE FROM exercise_replacements")
    fun deleteAll()
}
