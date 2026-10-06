package com.absforge.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.R
import com.absforge.ui.components.AbsForgeButton
import com.absforge.ui.theme.*

@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heroPulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Pill Badge
        Surface(
            color = AbsForgePrimary.copy(alpha = 0.15f),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, AbsForgePrimary.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = AbsForgePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "THE 30-DAY ABS BLUEPRINT",
                    color = AbsForgePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hero Card Visual Box
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { it / 3 },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 280.dp, max = 340.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF241517),
                                Color(0xFF141618),
                                Color(0xFF0C0D0E)
                            ),
                            radius = 600f
                        )
                    )
                    .border(1.dp, Color(0x33FF3030), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Background Ambient Glow
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    AbsForgePrimary.copy(alpha = 0.22f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Hero Logo / Badge
                Image(
                    painter = painterResource(id = R.drawable.hero_welcome_badge),
                    contentDescription = "AbsForge 30-Day Badge",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(230.dp)
                        .scale(pulseScale)
                )

                // Floating feature badges
                Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                    // Top Left Pill
                    FeaturePill(
                        icon = Icons.Filled.Bolt,
                        text = "30-Day Plan",
                        modifier = Modifier.align(Alignment.TopStart)
                    )

                    // Bottom Left Pill
                    FeaturePill(
                        icon = Icons.Filled.WifiOff,
                        text = "100% Offline",
                        modifier = Modifier.align(Alignment.BottomStart)
                    )

                    // Bottom Right Pill
                    FeaturePill(
                        icon = Icons.Filled.Headphones,
                        text = "Studio Coach",
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title and Value Proposition
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "BUILD YOUR ABS\nIN 30 DAYS",
                color = AbsForgeTextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 38.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Scientifically structured 10–20 min workouts.\nHD 3D animations & human voice coach.",
                color = AbsForgeTextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick 3-Proof Stats Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AbsForgeSurface)
                    .border(1.dp, AbsForgeGhostBorder, RoundedCornerShape(16.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickProofStat(value = "30", label = "DAYS")
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(AbsForgeGhostBorder))
                QuickProofStat(value = "10-20", label = "MIN / DAY")
                Box(modifier = Modifier.width(1.dp).height(24.dp).background(AbsForgeGhostBorder))
                QuickProofStat(value = "0", label = "EQUIPMENT")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // High-Impact CTA
            AbsForgeButton(
                onClick = onGetStarted,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "GET STARTED",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "→", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "✓ 100% Free • Works Offline • No Account Needed",
                color = AbsForgeTextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FeaturePill(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color(0xDD121316),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x33FFFFFF)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AbsForgePrimary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun QuickProofStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = AbsForgePrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = label,
            color = AbsForgeTextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}
