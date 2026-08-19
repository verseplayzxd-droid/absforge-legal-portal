package com.absforge.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.ui.theme.*

const val PRIVACY_POLICY_URL = "https://absforge.app/privacy-policy.html"
const val TERMS_OF_SERVICE_URL = "https://absforge.app/terms-of-service.html"
const val DEVELOPER_SUPPORT_EMAIL = "support@absforge.app"

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
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
                text = "PRIVACY POLICY",
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
                text = "AbsForge – 30 Day Abs Workout",
                color = AbsForgePrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Last updated: August 11, 2026",
                color = AbsForgeTextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            PolicySection(
                title = "1. Information Processed by AbsForge",
                body = "AbsForge is designed to store your workout progress, completed sessions, current streak, custom preferences, and achievements locally on your device. We do not require account registration or store your personal fitness logs on external servers."
            )

            PolicySection(
                title = "2. Advertising & Third-Party SDKs",
                body = "AbsForge uses Google AdMob to display advertisements within the application. The Google Mobile Ads SDK may process device identifiers, IP addresses, network status, and usage information required for ad delivery, measurement, fraud prevention, and diagnostic reporting in compliance with Google's Advertising Policies."
            )

            PolicySection(
                title = "3. Device Information",
                body = "To deliver optimized adaptive banners, video ads, and native content, Google AdMob and the User Messaging Platform (UMP) process non-sensitive device characteristics such as operating system version, device model, and screen parameters."
            )

            PolicySection(
                title = "4. Consent & Privacy Choices",
                body = "In applicable regions (including the EEA and UK), you are presented with a consent form powered by Google's User Messaging Platform (UMP) upon initial launch. You can inspect or update your privacy and ad personalization choices at any time via Settings -> Legal & Privacy -> Privacy Options."
            )

            PolicySection(
                title = "5. Local Fitness Data vs Advertising Data",
                body = "Your personal workout history, weight logs, and daily challenge progress remain isolated in an encrypted local database on your device and are never shared with advertising networks or third parties."
            )

            PolicySection(
                title = "6. Data Retention",
                body = "Locally stored fitness data remains on your device until you choose to clear your data within the application settings, clear local storage via Android system settings, or uninstall the app."
            )

            PolicySection(
                title = "7. Data Deletion",
                body = "You can delete all locally saved workout history, progress records, and personal settings at any time by navigating to Settings -> Legal & Privacy -> Delete Local Data."
            )

            PolicySection(
                title = "8. Children's Privacy",
                body = "AbsForge is intended for a general audience and is not designed specifically for children under the age of 13. We do not knowingly collect personal information from children."
            )

            PolicySection(
                title = "9. Policy Updates",
                body = "We may update this Privacy Policy periodically to reflect changes in legal requirements or service functionality. Any updates will be accessible directly through the application."
            )

            PolicySection(
                title = "10. Contact Us",
                body = "If you have any questions or feedback regarding this Privacy Policy, please contact developer support at:\n$DEVELOPER_SUPPORT_EMAIL"
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun PolicySection(title: String, body: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AbsForgeSurface),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = AbsForgeTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                color = AbsForgeTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}
