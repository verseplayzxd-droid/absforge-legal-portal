package com.absforge.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

object SoundManager {

    private const val TAG = "SoundManager"
    private const val SAMPLE_RATE = 44100
    private val scope = CoroutineScope(Dispatchers.Default)

    var soundEffectsEnabled = true
    var countdownSoundsEnabled = true

    fun playTone(frequencyHz: Double, durationMs: Int, volume: Float = 0.35f) {
        if (!soundEffectsEnabled) return
        scope.launch {
            try {
                val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
                if (numSamples <= 0) return@launch

                val buffer = ShortArray(numSamples)
                val decaySamples = (numSamples * 0.3).toInt()

                for (i in 0 until numSamples) {
                    val angle = 2.0 * PI * i * frequencyHz / SAMPLE_RATE
                    var envelope = 1.0
                    if (i < numSamples * 0.1) {
                        envelope = i / (numSamples * 0.1)
                    } else if (i > numSamples - decaySamples) {
                        envelope = (numSamples - i).toDouble() / decaySamples
                    }
                    val sample = (sin(angle) * Short.MAX_VALUE * volume * envelope).toInt()
                    buffer[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                }

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()
                audioTrack.setNotificationMarkerPosition(numSamples)
                audioTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                    override fun onMarkerReached(track: AudioTrack?) {
                        track?.release()
                    }
                    override fun onPeriodicNotification(track: AudioTrack?) {}
                })
            } catch (e: Exception) {
                Log.e(TAG, "Error playing tone: ${e.message}")
            }
        }
    }

    fun playDualTone(freq1: Double, freq2: Double, durationMs: Int, volume: Float = 0.35f) {
        if (!soundEffectsEnabled) return
        scope.launch {
            playTone(freq1, durationMs / 2, volume)
            delay((durationMs / 2).toLong())
            playTone(freq2, durationMs / 2, volume)
        }
    }

    // --- PREDEFINED SOUND EFFECTS ---
    fun playButtonClick() {
        playTone(1200.0, 15, 0.15f)
    }

    fun playWorkoutStart() {
        playDualTone(600.0, 900.0, 120, 0.35f)
    }

    fun playPause() {
        playDualTone(750.0, 500.0, 100, 0.3f)
    }

    fun playResume() {
        playDualTone(500.0, 750.0, 100, 0.3f)
    }

    fun playExerciseStart() {
        playTone(880.0, 80, 0.35f)
    }

    fun playExerciseComplete() {
        playDualTone(523.25, 659.25, 160, 0.4f)
    }

    fun playRestStart() {
        playTone(440.0, 100, 0.25f)
    }

    fun playRestEnd() {
        playDualTone(660.0, 880.0, 140, 0.35f)
    }

    fun playCountdownTick() {
        if (!countdownSoundsEnabled || !soundEffectsEnabled) return
        playTone(1000.0, 20, 0.25f)
    }

    fun playCountdownGo() {
        if (!countdownSoundsEnabled || !soundEffectsEnabled) return
        playTone(1046.5, 150, 0.45f)
    }

    fun playWorkoutComplete() {
        if (!soundEffectsEnabled) return
        scope.launch {
            playTone(523.25, 100, 0.4f)
            delay(100)
            playTone(659.25, 100, 0.4f)
            delay(100)
            playTone(783.99, 100, 0.4f)
            delay(100)
            playTone(1046.50, 200, 0.5f)
        }
    }

    fun playAchievementUnlock() {
        if (!soundEffectsEnabled) return
        scope.launch {
            playTone(587.33, 80, 0.35f)
            delay(80)
            playTone(739.99, 80, 0.35f)
            delay(80)
            playTone(880.00, 150, 0.4f)
        }
    }
}
