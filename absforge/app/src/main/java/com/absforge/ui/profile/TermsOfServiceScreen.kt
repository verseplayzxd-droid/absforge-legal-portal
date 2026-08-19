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

@Composable
fun TermsOfServiceScreen(
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
                text = "TERMS OF SERVICE",
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
                text = "AbsForge Terms of Service",
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
                title = "1. Acceptance of Terms",
                body = "By installing or using AbsForge, you agree to be bound by these Terms of Service. If you do not agree to these terms, please do not use the application."
            )

            PolicySection(
                title = "2. Fitness Service Guidance",
                body = "AbsForge provides general core fitness instruction, animation guides, and 30-day exercise routines for informational and personal fitness tracking purposes."
            )

            PolicySection(
                title = "3. No Guaranteed Results",
                body = "Individual physical fitness outcomes vary depending on genetics, diet, overall physical health, and exercise consistency. AbsForge does not guarantee specific body transformation or weight loss results."
            )

            PolicySection(
                title = "4. Health & Safety Disclaimer",
                body = "AbsForge is not a substitute for medical advice or professional personal training. You are responsible for exercising within your own physical limits. Immediately stop exercising and consult a medical professional if you feel pain, faintness, or shortness of breath."
            )

            PolicySection(
                title = "5. Advertisements",
                body = "AbsForge is supported by advertisements provided through Google AdMob. Third-party advertisers may display sponsored content, banners, and video ads in accordance with Google policies."
            )

            PolicySection(
                title = "6. Intellectual Property",
                body = "All visual assets, workout algorithms, code, graphics, branding, and interfaces in AbsForge are protected by applicable intellectual property laws."
            )

            PolicySection(
                title = "7. Service Availability & Changes",
                body = "We reserve the right to modify, update, or discontinue features or parts of the application at any time without prior notice."
            )

            PolicySection(
                title = "8. Contact Support",
                body = "For questions regarding these Terms of Service, please contact:\n$DEVELOPER_SUPPORT_EMAIL"
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
