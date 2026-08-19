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
fun HealthDisclaimerScreen(
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
                text = "HEALTH DISCLAIMER",
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
                text = "Important Health & Fitness Information",
                color = AbsForgePrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            PolicySection(
                title = "General Fitness Guidance Only",
                body = "AbsForge is designed strictly for general fitness and exercise instruction. The workout plans, animations, and guidance provided within this app do not constitute medical, health, or diagnostic advice."
            )

            PolicySection(
                title = "Physical Risk & Personal Responsibility",
                body = "Physical exercise carries inherent risks of injury. By utilizing AbsForge, you acknowledge that you are voluntarily participating in physical activities and assume full responsibility for your own safety and well-being."
            )

            PolicySection(
                title = "Consultation with Professionals",
                body = "If you have pre-existing medical conditions, heart conditions, spinal injuries, high blood pressure, or are recovering from surgery, you should consult a licensed physician or healthcare professional before beginning any new workout regimen."
            )

            PolicySection(
                title = "Listen to Your Body",
                body = "Never push beyond your personal physical capabilities. If you experience dizziness, nausea, shortness of breath, chest pain, or joint discomfort at any point during a workout, STOP EXERCISING IMMEDIATELY and seek medical attention if symptoms persist."
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
