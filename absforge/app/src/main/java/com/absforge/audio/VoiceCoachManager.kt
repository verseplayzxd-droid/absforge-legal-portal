package com.absforge.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.annotation.RawRes
import com.absforge.R
import java.util.Locale

class VoiceCoachManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false
    private val appContext = context.applicationContext

    init {
        try {
            tts = TextToSpeech(appContext, this)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize TextToSpeech: ${e.message}")
            isTtsInitialized = false
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            try {
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w(TAG, "US English TTS language is missing or not supported")
                } else {
                    isTtsInitialized = true
                    Log.d(TAG, "TextToSpeech initialized successfully")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting TTS language: ${e.message}")
                isTtsInitialized = false
            }
        } else {
            Log.w(TAG, "TTS Initialization failed with status code $status")
            isTtsInitialized = false
        }
    }

    @Synchronized
    private fun playRawAudio(@RawRes resId: Int, fallbackText: String? = null) {
        if (!voiceGuidanceEnabled) return

        try {
            stopCurrentAudio()

            val mp = MediaPlayer.create(appContext, resId)
            if (mp != null) {
                mp.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                )
                mp.setOnCompletionListener { player ->
                    try {
                        player.release()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error releasing MediaPlayer: ${e.message}")
                    }
                    if (mediaPlayer == player) {
                        mediaPlayer = null
                    }
                }
                mp.setOnErrorListener { player, what, extra ->
                    Log.w(TAG, "MediaPlayer error ($what, $extra), falling back to TTS")
                    try {
                        player.release()
                    } catch (e: Exception) {
                        // ignore
                    }
                    if (mediaPlayer == player) {
                        mediaPlayer = null
                    }
                    if (!fallbackText.isNullOrBlank()) {
                        speakTts(fallbackText)
                    }
                    true
                }
                mediaPlayer = mp
                mp.start()
            } else if (!fallbackText.isNullOrBlank()) {
                speakTts(fallbackText)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing raw audio: ${e.message}", e)
            if (!fallbackText.isNullOrBlank()) {
                speakTts(fallbackText)
            }
        }
    }

    @Synchronized
    fun stopCurrentAudio() {
        try {
            mediaPlayer?.let { mp ->
                if (mp.isPlaying) {
                    mp.stop()
                }
                mp.release()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping MediaPlayer: ${e.message}")
        } finally {
            mediaPlayer = null
        }
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping TTS: ${e.message}")
        }
    }

    @Synchronized
    fun pauseAudio() {
        try {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing audio: ${e.message}")
        }
    }

    @Synchronized
    fun resumeAudio() {
        try {
            mediaPlayer?.start()
        } catch (e: Exception) {
            Log.e(TAG, "Error resuming audio: ${e.message}")
        }
    }

    private fun speakTts(text: String) {
        if (isTtsInitialized && voiceGuidanceEnabled) {
            try {
                tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "AbsForgeVoice_${System.currentTimeMillis()}")
            } catch (e: Exception) {
                Log.e(TAG, "Error speaking text: ${e.message}")
            }
        }
    }

    fun speak(text: String) {
        speakTts(text)
    }

    // --- High-Quality Studio Voice Announcements ---

    fun announceExerciseStart(animationId: String, exerciseName: String) {
        val resId = exerciseAudioMap[animationId.lowercase()]
        if (resId != null) {
            playRawAudio(resId, fallbackText = "$exerciseName. Begin.")
        } else {
            speakTts("$exerciseName. Begin.")
        }
    }

    fun announceExerciseStart(exerciseName: String) {
        val animId = exerciseNameToKey(exerciseName)
        val resId = animId?.let { exerciseAudioMap[it] }
        if (resId != null) {
            playRawAudio(resId, fallbackText = "$exerciseName. Begin.")
        } else {
            speakTts("$exerciseName. Begin.")
        }
    }

    fun announceNextExercise(animationId: String, exerciseName: String) {
        val resId = nextExerciseAudioMap[animationId.lowercase()]
        if (resId != null) {
            playRawAudio(resId, fallbackText = "Next exercise: $exerciseName.")
        } else {
            speakTts("Next exercise: $exerciseName.")
        }
    }

    fun announceNextExercise(name: String) {
        val animId = exerciseNameToKey(name)
        val resId = animId?.let { nextExerciseAudioMap[it] }
        if (resId != null) {
            playRawAudio(resId, fallbackText = "Next exercise: $name.")
        } else {
            speakTts("Next exercise: $name.")
        }
    }

    fun announceHalfway() {
        playRawAudio(R.raw.voice_prompt_halfway, fallbackText = "Halfway there, keep going!")
    }

    fun announceTenSeconds() {
        playRawAudio(R.raw.voice_prompt_10s_left, fallbackText = "10 seconds left.")
    }

    fun announceFiveSeconds() {
        playRawAudio(R.raw.voice_prompt_5s_left, fallbackText = "5 seconds.")
    }

    fun announceThreeTwoOneRest() {
        playRawAudio(R.raw.voice_prompt_321, fallbackText = "3... 2... 1... rest.")
    }

    fun announceRestStart() {
        playRawAudio(R.raw.voice_prompt_rest, fallbackText = "Rest.")
    }

    fun announceRecover() {
        playRawAudio(R.raw.voice_prompt_recover, fallbackText = "Take a breath and recover.")
    }

    fun announceKeepGoing() {
        playRawAudio(R.raw.voice_prompt_keep_going, fallbackText = "Keep going, you got this!")
    }

    fun announceLastOne() {
        playRawAudio(R.raw.voice_prompt_last_one, fallbackText = "Last one, finish strong!")
    }

    fun announceWorkoutComplete() {
        playRawAudio(R.raw.voice_prompt_workout_complete_1, fallbackText = "Workout complete. Great work!")
    }

    fun announceCoolDown() {
        playRawAudio(R.raw.voice_prompt_cool_down, fallbackText = "Time to cool down.")
    }

    fun announceGetReady() {
        playRawAudio(R.raw.voice_prompt_get_ready, fallbackText = "Get ready.")
    }

    fun shutdown() {
        stopCurrentAudio()
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

        private val exerciseAudioMap = mapOf(
            "crunch" to R.raw.voice_ex_01_crunch,
            "v_crunch" to R.raw.voice_ex_02_v_crunch,
            "toe_touch" to R.raw.voice_ex_03_toe_touch,
            "knee_to_chest_crunch" to R.raw.voice_ex_04_knee_to_chest_crunch,
            "long_arm_crunch" to R.raw.voice_ex_05_long_arm_crunch,
            "cross_arm_crunch" to R.raw.voice_ex_06_cross_arm_crunch,
            "sit_up_twist" to R.raw.voice_ex_07_sit_up_twist,
            "reverse_crunch" to R.raw.voice_ex_08_reverse_crunch,
            "leg_raise" to R.raw.voice_ex_09_leg_raise,
            "flutter_kicks" to R.raw.voice_ex_10_flutter_kicks,
            "single_leg_drops" to R.raw.voice_ex_11_single_leg_drops,
            "leg_in_out" to R.raw.voice_ex_12_leg_in_out,
            "scissor_kicks" to R.raw.voice_ex_13_scissor_kicks,
            "plank" to R.raw.voice_ex_14_plank,
            "mountain_climbers" to R.raw.voice_ex_15_mountain_climbers,
            "v_up" to R.raw.voice_ex_16_v_up,
            "bicycle_crunch" to R.raw.voice_ex_17_bicycle_crunch,
            "high_knees" to R.raw.voice_ex_18_high_knees,
            "dead_bug" to R.raw.voice_ex_19_dead_bug,
            "bird_dog" to R.raw.voice_ex_20_bird_dog,
            "seated_abs_circles_cw" to R.raw.voice_ex_21_seated_abs_circles_cw,
            "russian_twist" to R.raw.voice_ex_22_russian_twist,
            "heel_touch" to R.raw.voice_ex_23_heel_touch,
            "side_plank_left" to R.raw.voice_ex_24_side_plank_left,
            "side_plank_right" to R.raw.voice_ex_25_side_plank_right,
            "bent_leg_twist" to R.raw.voice_ex_26_bent_leg_twist,
            "oblique_crunch" to R.raw.voice_ex_27_oblique_crunch,
            "standing_bicycle_crunch" to R.raw.voice_ex_28_standing_bicycle_crunch,
            "seated_abs_circles_ccw" to R.raw.voice_ex_29_seated_abs_circles_ccw,
            "cobra_stretch" to R.raw.voice_ex_30_cobra_stretch,
            "childs_pose" to R.raw.voice_ex_31_childs_pose,
            "lying_twist_stretch_left" to R.raw.voice_ex_32_lying_twist_stretch_left,
            "lying_twist_stretch_right" to R.raw.voice_ex_33_lying_twist_stretch_right
        )

        private val nextExerciseAudioMap = mapOf(
            "crunch" to R.raw.voice_next_01_crunch,
            "v_crunch" to R.raw.voice_next_02_v_crunch,
            "toe_touch" to R.raw.voice_next_03_toe_touch,
            "knee_to_chest_crunch" to R.raw.voice_next_04_knee_to_chest_crunch,
            "long_arm_crunch" to R.raw.voice_next_05_long_arm_crunch,
            "cross_arm_crunch" to R.raw.voice_next_06_cross_arm_crunch,
            "sit_up_twist" to R.raw.voice_next_07_sit_up_twist,
            "reverse_crunch" to R.raw.voice_next_08_reverse_crunch,
            "leg_raise" to R.raw.voice_next_09_leg_raise,
            "flutter_kicks" to R.raw.voice_next_10_flutter_kicks,
            "single_leg_drops" to R.raw.voice_next_11_single_leg_drops,
            "leg_in_out" to R.raw.voice_next_12_leg_in_out,
            "scissor_kicks" to R.raw.voice_next_13_scissor_kicks,
            "plank" to R.raw.voice_next_14_plank,
            "mountain_climbers" to R.raw.voice_next_15_mountain_climbers,
            "v_up" to R.raw.voice_next_16_v_up,
            "bicycle_crunch" to R.raw.voice_next_17_bicycle_crunch,
            "high_knees" to R.raw.voice_next_18_high_knees,
            "dead_bug" to R.raw.voice_next_19_dead_bug,
            "bird_dog" to R.raw.voice_next_20_bird_dog,
            "seated_abs_circles_cw" to R.raw.voice_next_21_seated_abs_circles_cw,
            "russian_twist" to R.raw.voice_next_22_russian_twist,
            "heel_touch" to R.raw.voice_next_23_heel_touch,
            "side_plank_left" to R.raw.voice_next_24_side_plank_left,
            "side_plank_right" to R.raw.voice_next_25_side_plank_right,
            "bent_leg_twist" to R.raw.voice_next_26_bent_leg_twist,
            "oblique_crunch" to R.raw.voice_next_27_oblique_crunch,
            "standing_bicycle_crunch" to R.raw.voice_next_28_standing_bicycle_crunch,
            "seated_abs_circles_ccw" to R.raw.voice_next_29_seated_abs_circles_ccw,
            "cobra_stretch" to R.raw.voice_next_30_cobra_stretch,
            "childs_pose" to R.raw.voice_next_31_childs_pose,
            "lying_twist_stretch_left" to R.raw.voice_next_32_lying_twist_stretch_left,
            "lying_twist_stretch_right" to R.raw.voice_next_33_lying_twist_stretch_right
        )

        private fun exerciseNameToKey(name: String): String? {
            val normalized = name.lowercase().trim()
                .replace(" ", "_")
                .replace("-", "_")
            return exerciseAudioMap.keys.find { key ->
                key == normalized || normalized.contains(key) || key.contains(normalized)
            }
        }
    }
}
