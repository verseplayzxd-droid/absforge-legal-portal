package com.absforge.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.absforge.data.local.entity.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements")
    fun getAll(): Flow<List<AchievementEntity>>

    @Query("UPDATE achievements SET isUnlocked = 1, unlockedAt = :unlockedAt WHERE id = :id")
    fun unlock(id: String, unlockedAt: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(achievements: List<AchievementEntity>)

    @Query("SELECT COUNT(*) FROM achievements")
    fun getCount(): Int

    @Query("DELETE FROM achievements")
    fun deleteAll()
}
