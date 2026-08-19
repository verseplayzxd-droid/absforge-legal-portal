package com.absforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.absforge.data.local.entity.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Int): WorkoutSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: WorkoutSessionEntity): Long

    @Update
    suspend fun updateSession(session: WorkoutSessionEntity)

    @Query("SELECT * FROM workout_sessions WHERE planId = :planId")
    fun getSessionsForPlan(planId: Int): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT * FROM workout_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("SELECT dayNumber FROM workout_sessions WHERE planId = :planId")
    fun getCompletedDays(planId: Int): Flow<List<Int>>

    @Query("SELECT * FROM workout_sessions WHERE planId = :planId AND dayNumber = :dayNumber LIMIT 1")
    fun getSessionByDay(planId: Int, dayNumber: Int): WorkoutSessionEntity?

    @Query("SELECT SUM(caloriesBurned) FROM workout_sessions")
    fun getTotalCalories(): Flow<Int?>

    @Query("SELECT SUM(durationSeconds) / 60 FROM workout_sessions")
    fun getTotalMinutes(): Flow<Int?>

    @Query("SELECT COUNT(*) FROM workout_sessions")
    fun getTotalWorkouts(): Flow<Int?>

    @Query("SELECT * FROM workout_sessions WHERE startTime >= :startTime AND startTime <= :endTime")
    fun getSessionsInRange(startTime: Long, endTime: Long): Flow<List<WorkoutSessionEntity>>
}
