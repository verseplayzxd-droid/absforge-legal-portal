package com.absforge.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "absforge_prefs")

class PreferencesManager(private val context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val SELECTED_PLAN_ID = intPreferencesKey("selected_plan_id")
        val CURRENT_DAY = intPreferencesKey("current_day")
        val DEFAULT_REST_SECONDS = intPreferencesKey("default_rest_seconds")
        val VOICE_GUIDANCE_ENABLED = booleanPreferencesKey("voice_guidance_enabled")
        val SOUND_EFFECTS_ENABLED = booleanPreferencesKey("sound_effects_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val AUTO_START_NEXT = booleanPreferencesKey("auto_start_next")
        val COUNTDOWN_ENABLED = booleanPreferencesKey("countdown_enabled")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_HOUR = intPreferencesKey("reminder_hour")
        val REMINDER_MINUTE = intPreferencesKey("reminder_minute")
        val ADS_ENABLED = booleanPreferencesKey("ads_enabled")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
        val STRONG_REMINDER_ENABLED = booleanPreferencesKey("strong_reminder_enabled")

        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_NAME = stringPreferencesKey("user_name")
        val AUTH_PROVIDER = stringPreferencesKey("auth_provider")
    }

    val onboardingCompleted: Flow<Boolean> = dataStore.data.map { it[ONBOARDING_COMPLETED] ?: false }
    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[ONBOARDING_COMPLETED] = completed }
    }

    val selectedPlanId: Flow<Int> = dataStore.data.map { it[SELECTED_PLAN_ID] ?: -1 }
    suspend fun setSelectedPlanId(id: Int) {
        dataStore.edit { it[SELECTED_PLAN_ID] = id }
    }

    val currentDay: Flow<Int> = dataStore.data.map { it[CURRENT_DAY] ?: 1 }
    suspend fun setCurrentDay(day: Int) {
        dataStore.edit { it[CURRENT_DAY] = day }
    }

    val defaultRestSeconds: Flow<Int> = dataStore.data.map { it[DEFAULT_REST_SECONDS] ?: 30 }
    suspend fun setDefaultRestSeconds(seconds: Int) {
        dataStore.edit { it[DEFAULT_REST_SECONDS] = seconds }
    }

    val voiceGuidanceEnabled: Flow<Boolean> = dataStore.data.map { it[VOICE_GUIDANCE_ENABLED] ?: true }
    suspend fun setVoiceGuidanceEnabled(enabled: Boolean) {
        dataStore.edit { it[VOICE_GUIDANCE_ENABLED] = enabled }
    }

    val soundEffectsEnabled: Flow<Boolean> = dataStore.data.map { it[SOUND_EFFECTS_ENABLED] ?: true }
    suspend fun setSoundEffectsEnabled(enabled: Boolean) {
        dataStore.edit { it[SOUND_EFFECTS_ENABLED] = enabled }
    }

    val vibrationEnabled: Flow<Boolean> = dataStore.data.map { it[VIBRATION_ENABLED] ?: true }
    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { it[VIBRATION_ENABLED] = enabled }
    }

    val autoStartNext: Flow<Boolean> = dataStore.data.map { it[AUTO_START_NEXT] ?: false }
    suspend fun setAutoStartNext(enabled: Boolean) {
        dataStore.edit { it[AUTO_START_NEXT] = enabled }
    }

    val countdownEnabled: Flow<Boolean> = dataStore.data.map { it[COUNTDOWN_ENABLED] ?: true }
    suspend fun setCountdownEnabled(enabled: Boolean) {
        dataStore.edit { it[COUNTDOWN_ENABLED] = enabled }
    }

    val reminderEnabled: Flow<Boolean> = dataStore.data.map { it[REMINDER_ENABLED] ?: false }
    suspend fun setReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[REMINDER_ENABLED] = enabled }
    }

    val reminderHour: Flow<Int> = dataStore.data.map { it[REMINDER_HOUR] ?: 8 }
    suspend fun setReminderHour(hour: Int) {
        dataStore.edit { it[REMINDER_HOUR] = hour }
    }

    val reminderMinute: Flow<Int> = dataStore.data.map { it[REMINDER_MINUTE] ?: 0 }
    suspend fun setReminderMinute(minute: Int) {
        dataStore.edit { it[REMINDER_MINUTE] = minute }
    }

    val adsEnabled: Flow<Boolean> = dataStore.data.map { it[ADS_ENABLED] ?: true }
    suspend fun setAdsEnabled(enabled: Boolean) {
        dataStore.edit { it[ADS_ENABLED] = enabled }
    }

    val isPremium: Flow<Boolean> = dataStore.data.map { it[IS_PREMIUM] ?: false }
    suspend fun setIsPremium(premium: Boolean) {
        dataStore.edit { it[IS_PREMIUM] = premium }
    }

    val strongReminderEnabled: Flow<Boolean> = dataStore.data.map { it[STRONG_REMINDER_ENABLED] ?: false }
    suspend fun setStrongReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[STRONG_REMINDER_ENABLED] = enabled }
    }
}
