package com.absforge.audio

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

object HapticManager {
    var vibrationEnabled = true

    private fun getVibrator(context: Context): Vibrator? {
        if (!vibrationEnabled) return null
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun countdownTick(context: Context) {
        try {
            getVibrator(context)?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun exerciseComplete(context: Context) {
        try {
            getVibrator(context)?.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun workoutComplete(context: Context) {
        try {
            val pattern = longArrayOf(0, 100, 50, 100, 50, 300)
            getVibrator(context)?.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun buttonPress(context: Context) {
        try {
            getVibrator(context)?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
