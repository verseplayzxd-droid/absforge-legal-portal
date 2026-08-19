package com.absforge.ui.profile

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.ads.GoogleMobileAdsConsentManager
import com.absforge.ui.theme.*

@Composable
fun PrivacyDataScreen(
    onBack: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToDisclaimer: () -> Unit,
    onDataDeleted: () -> Unit
) {
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val consentManager = remember { GoogleMobileAdsConsentManager.getInstance(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AbsForgeTextPrimary)
            }
            Text(
                text = "LEGAL & PRIVACY",
                color = AbsForgeTextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                text = "LEGAL DOCUMENTS",
                color = AbsForgePrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LegalRow(title = "Privacy Policy", icon = Icons.Default.Description, onClick = onNavigateToPrivacyPolicy)
            LegalRow(title = "Terms of Service", icon = Icons.Default.Gavel, onClick = onNavigateToTerms)
            LegalRow(title = "Health & Fitness Disclaimer", icon = Icons.Default.HealthAndSafety, onClick = onNavigateToDisclaimer)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "PRIVACY & CHOICES",
                color = AbsForgePrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LegalRow(
                title = "Privacy Options (Ad Consent)",
                icon = Icons.Default.Tune,
                onClick = {
                    val activity = context.findActivity()
                    if (activity != null) {
                        consentManager.showPrivacyOptionsForm(activity) { _ -> }
                    }
                }
            )

            LegalRow(
                title = "Delete Local Data",
                icon = Icons.Default.DeleteForever,
                iconTint = AbsForgePrimary,
                onClick = { showDeleteDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "APPLICATION",
                color = AbsForgePrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LegalRow(
                title = "About AbsForge",
                icon = Icons.Default.Info,
                onClick = { showAboutDialog = true }
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                containerColor = AbsForgeSurface,
                title = { Text("Delete Local Data?", color = AbsForgeTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "This will permanently clear your local workout history, streak count, and settings from your device. This action cannot be undone.",
                        color = AbsForgeTextSecondary,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            onDataDeleted()
                        }
                    ) {
                        Text("DELETE", color = AbsForgePrimary, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("CANCEL", color = AbsForgeTextPrimary)
                    }
                }
            )
        }

        if (showAboutDialog) {
            AlertDialog(
                onDismissRequest = { showAboutDialog = false },
                containerColor = AbsForgeSurface,
                title = { Text("AbsForge v1.0.0", color = AbsForgeTextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "AbsForge – 30 Day Abs Workout\n\nDesigned for extreme core transformation. Featuring vector animations, Room local persistence, and Google Mobile Ads SDK integration.",
                        color = AbsForgeTextSecondary,
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showAboutDialog = false }) {
                        Text("CLOSE", color = AbsForgePrimary, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun LegalRow(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color = AbsForgePrimary,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AbsForgeSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, color = AbsForgeTextPrimary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = AbsForgeTextSecondary)
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
