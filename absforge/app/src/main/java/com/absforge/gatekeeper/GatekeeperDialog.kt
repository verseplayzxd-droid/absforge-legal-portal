package com.absforge.gatekeeper

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.absforge.BuildConfig
import com.absforge.ui.theme.AbsForgePrimary
import com.absforge.ui.theme.AbsForgeBackground
import com.absforge.ui.theme.AbsForgeBorder
import com.absforge.ui.theme.AbsForgeSurface
import com.absforge.ui.theme.AbsForgeTextTertiary
import com.absforge.ui.theme.AbsForgeTextPrimary
import com.absforge.ui.theme.AbsForgeTextSecondary

@Composable
fun GatekeeperDialog(
    state: GatekeeperState,
    config: GatekeeperConfig?,
    onUpdateClick: () -> Unit,
    onDismissFlexible: () -> Unit,
    onRetryMaintenance: () -> Unit
) {
    if (state == GatekeeperState.IDLE || state == GatekeeperState.CHECKING || config == null) {
        return
    }

    // Safety guard: if user already has the latest version or is above minimum, NEVER show update dialog!
    val currentCode = BuildConfig.VERSION_CODE
    if ((state == GatekeeperState.FORCE_UPDATE || state == GatekeeperState.FLEXIBLE_UPDATE) &&
        config.minVersionCode > 0 && currentCode >= config.minVersionCode &&
        (config.latestVersionCode <= 0 || currentCode >= config.latestVersionCode)
    ) {
        return
    }

    val isStrict = state == GatekeeperState.FORCE_UPDATE || state == GatekeeperState.MAINTENANCE

    // Trap Android system and gesture back navigation completely when in strict mode
    BackHandler(enabled = isStrict) {
        // Deliberately no-op: user cannot navigate away without updating or waiting for maintenance to end
    }

    Dialog(
        onDismissRequest = {
            if (!isStrict) {
                onDismissFlexible()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = !isStrict,
            dismissOnClickOutside = !isStrict,
            usePlatformDefaultWidth = false
        )
    ) {
        // Dimmed fullscreen backdrop to block all underlying UI touches
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.88f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp)),
                color = AbsForgeSurface,
                border = BorderStroke(
                    width = 1.5.dp,
                    color = if (state == GatekeeperState.FORCE_UPDATE) AbsForgePrimary else AbsForgeBorder
                ),
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Status Icon
                    val icon: ImageVector = when (state) {
                        GatekeeperState.MAINTENANCE -> Icons.Filled.Build
                        GatekeeperState.FORCE_UPDATE -> Icons.Filled.Warning
                        else -> Icons.Filled.SystemUpdate
                    }
                    val iconTint = when (state) {
                        GatekeeperState.MAINTENANCE -> Color(0xFFFFA000)
                        GatekeeperState.FORCE_UPDATE -> AbsForgePrimary
                        else -> Color(0xFF3B82F6)
                    }

                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(iconTint.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Title
                    val title = when (state) {
                        GatekeeperState.MAINTENANCE -> config.maintenanceTitle.ifBlank { "Under Maintenance 🛠️" }
                        else -> config.updateTitle.ifBlank { "Update Available! 🚀" }
                    }
                    Text(
                        text = title,
                        color = AbsForgeTextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Message
                    val message = when (state) {
                        GatekeeperState.MAINTENANCE -> config.maintenanceMessage.ifBlank {
                            "We are upgrading our servers to improve your fitness journey. Please check back shortly."
                        }
                        else -> config.updateMessage.ifBlank {
                            "A newer version of AbsForge is available on Google Play with new workouts and improvements."
                        }
                    }
                    Text(
                        text = message,
                        color = AbsForgeTextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    // Version Info Pill (if Update)
                    if (state == GatekeeperState.FORCE_UPDATE || state == GatekeeperState.FLEXIBLE_UPDATE) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = AbsForgeBackground,
                            border = BorderStroke(1.dp, AbsForgeBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Installed: v${BuildConfig.VERSION_NAME}",
                                    color = AbsForgeTextTertiary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                if (config.latestVersionName.isNotBlank()) {
                                    Text(
                                        text = " ➔ Latest: v${config.latestVersionName}",
                                        color = AbsForgePrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // Action Buttons
                    when (state) {
                        GatekeeperState.MAINTENANCE -> {
                            Button(
                                onClick = onRetryMaintenance,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AbsForgePrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "RETRY CONNECTION",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        GatekeeperState.FORCE_UPDATE -> {
                            Button(
                                onClick = onUpdateClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AbsForgePrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SystemUpdate,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "UPDATE NOW ON PLAY STORE",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        GatekeeperState.FLEXIBLE_UPDATE -> {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = onUpdateClick,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = AbsForgePrimary)
                                ) {
                                    Text(
                                        text = "UPDATE NOW",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }

                                OutlinedButton(
                                    onClick = onDismissFlexible,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    border = BorderStroke(1.dp, AbsForgeBorder),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = AbsForgeTextSecondary
                                    )
                                ) {
                                    Text(
                                        text = "LATER",
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}
