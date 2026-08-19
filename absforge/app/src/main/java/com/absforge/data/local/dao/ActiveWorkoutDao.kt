package com.absforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.absforge.data.local.entity.ActiveWorkoutStateEntity

@Dao
interface ActiveWorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(state: ActiveWorkoutStateEntity)

    @Query("SELECT * FROM active_workout_state WHERE id = 1")
    fun get(): ActiveWorkoutStateEntity?

    @Query("DELETE FROM active_workout_state")
    fun clear()
}
