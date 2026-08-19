package com.absforge

import android.app.Application
import android.util.Log
import com.absforge.data.local.AbsForgeDatabase
import com.absforge.data.preferences.PreferencesManager
import com.absforge.data.seed.DatabaseSeeder
import com.absforge.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class AbsForgeApplication : Application() {

    val database: AbsForgeDatabase by lazy {
        AbsForgeDatabase.getInstance(this)
    }

    val preferencesManager: PreferencesManager by lazy {
        PreferencesManager(this)
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Install a global uncaught exception handler for debugging
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("AbsForge_CRASH", "FATAL EXCEPTION on thread ${thread.name}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }

        NotificationHelper.createNotificationChannel(this)
        com.absforge.ads.AdMobManager.init(this)
        seedDatabaseIfNeeded()
    }

    private fun seedDatabaseIfNeeded() {
        applicationScope.launch {
            try {
                val seeder = DatabaseSeeder(database)
                seeder.seedIfNeeded()
            } catch (e: Exception) {
                Log.e("AbsForge_SEED", "Database seeding failed", e)
            }
        }
    }

    companion object {
        lateinit var instance: AbsForgeApplication
            private set
    }
}
