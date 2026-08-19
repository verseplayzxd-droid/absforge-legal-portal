# AbsForge ProGuard Rules

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Kotlin Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Keep data classes for Room
-keepclassmembers class com.absforge.data.local.entity.** {
    <fields>;
    <init>(...);
}

# Start.io SDK Rules
-keep class com.startapp.** { *; }
-dontwarn com.startapp.**

