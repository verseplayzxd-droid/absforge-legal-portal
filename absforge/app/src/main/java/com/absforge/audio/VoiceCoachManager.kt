package com.absforge.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class VoiceCoachManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize TextToSpeech: ${e.message}")
            isInitialized = false
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w(TAG, "US English TTS language is missing or not supported")
                } else {
                    isInitialized = true
                    Log.d(TAG, "TextToSpeech initialized successfully")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting TTS language: ${e.message}")
                isInitialized = false
            }
        } else {
            Log.w(TAG, "TTS Initialization failed with status code $status")
            isInitialized = false
        }
    }

    fun speak(text: String) {
        if (isInitialized && voiceGuidanceEnabled) {
            try {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AbsForgeVoice_${System.currentTimeMillis()}")
            } catch (e: Exception) {
                Log.e(TAG, "Error speaking text: ${e.message}")
            }
        }
    }

    fun announceExerciseStart(exerciseName: String) {
        speak("$exerciseName. Begin.")
    }

    fun announceTenSeconds() {
        speak("10 seconds left.")
    }

    fun announceThreeTwoOneRest() {
        speak("3... 2... 1... rest.")
    }

    fun announceRestStart() {
        speak("Rest.")
    }

    fun announceNextExercise(name: String) {
        speak("Next exercise: $name.")
    }

    fun announceWorkoutComplete() {
        speak("Workout complete. Great job.")
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down TTS: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "VoiceCoachManager"
        var voiceGuidanceEnabled = true
    }
}
