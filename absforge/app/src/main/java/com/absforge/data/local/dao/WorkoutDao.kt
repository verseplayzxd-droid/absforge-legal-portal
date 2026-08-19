package com.absforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.absforge.data.local.entity.WorkoutDayEntity
import com.absforge.data.local.entity.WorkoutDayExerciseEntity
import com.absforge.data.local.entity.WorkoutPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workout_plans WHERE id = :planId")
    fun getPlanById(planId: Int): WorkoutPlanEntity?

    @Query("SELECT * FROM workout_plans")
    fun getAllPlans(): Flow<List<WorkoutPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlan(plan: WorkoutPlanEntity): Long

    @Query("SELECT * FROM workout_days WHERE planId = :planId ORDER BY dayNumber ASC")
    fun getDaysForPlan(planId: Int): Flow<List<WorkoutDayEntity>>

    @Query("SELECT * FROM workout_days WHERE planId = :planId AND dayNumber = :dayNumber")
    fun getDayByNumber(planId: Int, dayNumber: Int): WorkoutDayEntity?

    @Query("SELECT * FROM workout_day_exercises WHERE dayId = :dayId ORDER BY orderIndex ASC")
    fun getExercisesForDay(dayId: Int): Flow<List<WorkoutDayExerciseEntity>>

    @Query("SELECT * FROM workout_day_exercises WHERE dayId = :dayId ORDER BY orderIndex ASC")
    fun getExercisesForDaySync(dayId: Int): List<WorkoutDayExerciseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDay(day: WorkoutDayEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDays(days: List<WorkoutDayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDayExercises(exercises: List<WorkoutDayExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlans(plans: List<WorkoutPlanEntity>)

    @Query("SELECT COUNT(*) FROM workout_plans")
    fun getPlanCount(): Int
}
