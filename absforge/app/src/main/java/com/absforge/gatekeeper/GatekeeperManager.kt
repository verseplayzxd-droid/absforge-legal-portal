package com.absforge.gatekeeper

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.absforge.BuildConfig
import com.absforge.data.preferences.PreferencesManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class GatekeeperManager private constructor(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _gatekeeperState = MutableStateFlow(GatekeeperState.IDLE)
    val gatekeeperState: StateFlow<GatekeeperState> = _gatekeeperState.asStateFlow()

    private val _currentConfig = MutableStateFlow<GatekeeperConfig?>(null)
    val currentConfig: StateFlow<GatekeeperConfig?> = _currentConfig.asStateFlow()

    companion object {
        private const val TAG = "GatekeeperManager"
        private const val BASE_URL = "https://forge-whiteboard.vercel.app/api/gatekeeper"
        private const val CONNECT_TIMEOUT_MS = 5000
        private const val READ_TIMEOUT_MS = 5000
        private const val SNOOZE_DURATION_MS = 24 * 60 * 60 * 1000L // 24 Hours

        @Volatile
        private var INSTANCE: GatekeeperManager? = null

        fun getInstance(context: Context, preferencesManager: PreferencesManager): GatekeeperManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: GatekeeperManager(context.applicationContext, preferencesManager).also {
                    INSTANCE = it
                }
            }
        }
    }

    /**
     * Triggers the remote version and maintenance check against the Central Master API.
     * Offline or failed responses will gracefully fallback without blocking or crashing the app.
     */
    fun checkRemoteConfig() {
        scope.launch {
            _gatekeeperState.value = GatekeeperState.CHECKING
            try {
                val config = fetchFromApi(BuildConfig.APPLICATION_ID)
                _currentConfig.value = config

                if (config == null) {
                    _gatekeeperState.value = GatekeeperState.IDLE
                    return@launch
                }

                val currentVersionCode = BuildConfig.VERSION_CODE
                Log.d(TAG, "Current Version: $currentVersionCode, Remote Min: ${config.minVersionCode}, Latest: ${config.latestVersionCode}")

                val isBelowMin = config.minVersionCode > 0 && currentVersionCode < config.minVersionCode
                val isBelowLatest = config.latestVersionCode > 0 && currentVersionCode < config.latestVersionCode
                val isOutdated = isBelowMin || isBelowLatest

                when {
                    // 1. Maintenance Mode has absolute priority
                    config.isMaintenance -> {
                        Log.w(TAG, "Maintenance mode active")
                        _gatekeeperState.value = GatekeeperState.MAINTENANCE
                    }

                    // 2. Force Update required ONLY if user is outdated AND (explicitly flagged or below minimum allowed)
                    isOutdated && (config.forceUpdate || isBelowMin) -> {
                        Log.w(TAG, "Force update required: current $currentVersionCode < min ${config.minVersionCode} or latest ${config.latestVersionCode}")
                        _gatekeeperState.value = GatekeeperState.FORCE_UPDATE
                    }

                    // 3. Flexible Update (Optional) if user is outdated AND (flagged or below latest version code)
                    isOutdated && (config.flexibleUpdate || isBelowLatest) -> {
                        val lastSnoozed = preferencesManager.flexibleUpdateSnoozeTimestamp.first()
                        val now = System.currentTimeMillis()
                        val isSnoozed = (now - lastSnoozed) < SNOOZE_DURATION_MS

                        if (isSnoozed) {
                            Log.d(TAG, "Flexible update available but snoozed for 24 hours")
                            _gatekeeperState.value = GatekeeperState.IDLE
                        } else {
                            Log.d(TAG, "Flexible update prompt shown")
                            _gatekeeperState.value = GatekeeperState.FLEXIBLE_UPDATE
                        }
                    }

                    else -> {
                        _gatekeeperState.value = GatekeeperState.IDLE
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gatekeeper check failed gracefully, proceeding with normal app flow", e)
                _gatekeeperState.value = GatekeeperState.IDLE
            }
        }
    }

    /**
     * Snooze flexible update prompt for 24 hours when user clicks "Later".
     */
    fun snoozeFlexibleUpdate() {
        scope.launch {
            try {
                preferencesManager.setFlexibleUpdateSnoozed(System.currentTimeMillis())
            } catch (e: Exception) {
                Log.e(TAG, "Failed to snooze flexible update", e)
            }
            _gatekeeperState.value = GatekeeperState.IDLE
        }
    }

    /**
     * Launches the Google Play Store directly to the app's listing page.
     */
    fun openPlayStore(context: Context, customUrl: String? = null) {
        val packageName = BuildConfig.APPLICATION_ID
        val targetUrl = customUrl?.takeIf { it.isNotBlank() }
            ?: "https://play.google.com/store/apps/details?id=$packageName"

        try {
            // Attempt to open native Google Play Store app directly
            val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                setPackage("com.android.vending")
            }
            context.startActivity(marketIntent)
        } catch (_: Exception) {
            // Fallback to web browser URL
            try {
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(webIntent)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to open Play Store link", e)
            }
        }
    }

    private suspend fun fetchFromApi(packageName: String): GatekeeperConfig? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val urlString = "$BASE_URL?package=$packageName"
            val url = URL(urlString)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = CONNECT_TIMEOUT_MS
                readTimeout = READ_TIMEOUT_MS
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "AbsForge-Android/${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                doInput = true
            }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "Gatekeeper API responded with HTTP $responseCode")
                return@withContext null
            }

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                response.append(line)
            }
            reader.close()

            val json = JSONObject(response.toString())
            parseConfigJson(json)
        } catch (e: Exception) {
            Log.w(TAG, "Error connecting to Gatekeeper API: ${e.message}")
            null
        } finally {
            connection?.disconnect()
        }
    }

    private fun parseConfigJson(json: JSONObject): GatekeeperConfig {
        return GatekeeperConfig(
            packageName = json.optString("package_name", BuildConfig.APPLICATION_ID),
            minVersionCode = json.optInt("min_version_code", 0),
            latestVersionCode = json.optInt("latest_version_code", 0),
            latestVersionName = json.optString("latest_version_name", ""),
            forceUpdate = json.optBoolean("force_update", false),
            flexibleUpdate = json.optBoolean("flexible_update", false),
            isMaintenance = json.optBoolean("is_maintenance", false),
            maintenanceTitle = json.optString("maintenance_title", "Under Maintenance 🛠️"),
            maintenanceMessage = json.optString(
                "maintenance_message",
                "We are upgrading our servers. Please check back shortly."
            ),
            updateTitle = json.optString("update_title", "New Update Available! 🚀"),
            updateMessage = json.optString(
                "update_message",
                "Please update to the latest version on Google Play for new features and bug fixes."
            ),
            playStoreUrl = json.optString(
                "play_store_url",
                "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"
            )
        )
    }
}
