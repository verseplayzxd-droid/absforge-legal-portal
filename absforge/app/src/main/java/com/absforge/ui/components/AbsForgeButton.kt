package com.absforge.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.absforge.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AbsForgeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hapticFeedbackEnabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1.0f, label = "buttonScale")

    val view = LocalView.current
    var isDebouncing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            if (!isDebouncing) {
                isDebouncing = true
                com.absforge.audio.SoundManager.playButtonClick()
                if (hapticFeedbackEnabled) {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
                onClick()
                coroutineScope.launch {
                    delay(300)
                    isDebouncing = false
                }
            }
        },
        modifier = modifier
            .height(54.dp)
            .scale(scale),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isPressed) AbsForgePrimaryPressed else AbsForgePrimary,
            contentColor = Color.White,
            disabledContainerColor = AbsForgeDisabledBackground,
            disabledContentColor = AbsForgeDisabledText
        ),
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 24.dp),
        content = content
    )
}

@Composable
fun AbsForgeOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hapticFeedbackEnabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.97f else 1.0f, label = "outlinedButtonScale")

    val view = LocalView.current
    var isDebouncing by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    OutlinedButton(
        onClick = {
            if (!isDebouncing) {
                isDebouncing = true
                if (hapticFeedbackEnabled) {
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
                onClick()
                coroutineScope.launch {
                    delay(300)
                    isDebouncing = false
                }
            }
        },
        modifier = modifier
            .height(54.dp)
            .scale(scale),
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = AbsForgeSurfaceElevated,
            contentColor = AbsForgeTextPrimary,
            disabledContentColor = AbsForgeDisabledText
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) AbsForgeBorder else AbsForgeBorder.copy(alpha = 0.5f)
        ),
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 24.dp),
        content = content
    )
}

@Composable
fun AbsForgeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hapticFeedbackEnabled: Boolean = true
) {
    AbsForgeButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        hapticFeedbackEnabled = hapticFeedbackEnabled
    ) {
        androidx.compose.material3.Text(
            text = text,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}

@Composable
fun AbsForgeOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hapticFeedbackEnabled: Boolean = true
) {
    AbsForgeOutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        hapticFeedbackEnabled = hapticFeedbackEnabled
    ) {
        androidx.compose.material3.Text(
            text = text,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}
