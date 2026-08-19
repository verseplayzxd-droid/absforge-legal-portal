package com.absforge.ui.theme

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AbsForgePrimary,
    onPrimary = AbsForgeOnPrimary,
    background = AbsForgeBackground,
    onBackground = AbsForgeTextPrimary,
    surface = AbsForgeSurface,
    onSurface = AbsForgeTextPrimary,
    surfaceVariant = AbsForgeSurfaceContainer,
    onSurfaceVariant = AbsForgeTextSecondary,
    error = AbsForgeError,
    onError = AbsForgeOnError
)

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

@Composable
fun AbsForgeTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context.findActivity()
            if (activity != null) {
                try {
                    val window = activity.window
                    window.statusBarColor = android.graphics.Color.TRANSPARENT
                    window.navigationBarColor = android.graphics.Color.TRANSPARENT
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    val windowInsetsController = WindowCompat.getInsetsController(window, view)
                    windowInsetsController.isAppearanceLightStatusBars = false
                    windowInsetsController.isAppearanceLightNavigationBars = false
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
