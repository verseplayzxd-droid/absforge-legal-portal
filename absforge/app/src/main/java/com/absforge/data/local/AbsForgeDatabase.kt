package com.absforge.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.absforge.data.local.dao.*
import com.absforge.data.local.entity.*

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutPlanEntity::class,
        WorkoutDayEntity::class,
        WorkoutDayExerciseEntity::class,
        WorkoutSessionEntity::class,
        UserProfileEntity::class,
        WeightEntryEntity::class,
        BodyMeasurementEntity::class,
        AchievementEntity::class,
        FavoriteExerciseEntity::class,
        ProgressPhotoEntity::class,
        ExerciseReplacementEntity::class,
        ActiveWorkoutStateEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AbsForgeDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutDao(): WorkoutDao
    fun planDao(): WorkoutDao = workoutDao()
    abstract fun sessionDao(): SessionDao
    fun workoutSessionDao(): SessionDao = sessionDao()
    abstract fun userProfileDao(): UserProfileDao
    abstract fun weightDao(): WeightDao
    abstract fun measurementDao(): MeasurementDao
    abstract fun achievementDao(): AchievementDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun photoDao(): PhotoDao
    abstract fun replacementDao(): ReplacementDao
    abstract fun activeWorkoutDao(): ActiveWorkoutDao

    companion object {
        @Volatile
        private var INSTANCE: AbsForgeDatabase? = null

        fun getInstance(context: Context): AbsForgeDatabase = getDatabase(context)

        fun getDatabase(context: Context): AbsForgeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AbsForgeDatabase::class.java,
                    "absforge_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
