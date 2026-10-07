package com.absforge.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.AbsForgeApplication
import com.absforge.ads.AdMobManager
import com.absforge.payment.CashfreeConfig
import com.absforge.payment.CashfreePaymentDialog
import com.absforge.payment.PremiumPlan
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as AbsForgeApplication
    val preferencesManager = app.preferencesManager
    val scope = rememberCoroutineScope()

    val isPremiumUser by preferencesManager.isPremium.collectAsState(initial = AdMobManager.isPremium())

    // 0: 1 Week ($7), 1: 1 Month ($20), 2: Permanent ($30)
    var selectedPlanIndex by remember { mutableIntStateOf(2) } // default to Permanent (Best Value)
    var showPaymentDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var lastPurchasedPlan by remember { mutableStateOf<PremiumPlan?>(null) }

    val bgColor = Color(0xFF090B0A)
    val limeColor = Color(0xFFB7FF00)

    val currentPlan = CashfreeConfig.PLANS.getOrElse(selectedPlanIndex) { CashfreeConfig.PLANS.last() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "REMOVE ADS",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = bgColor)
            )
        },
        containerColor = bgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isPremiumUser) {
                // --- ACTIVE PREMIUM BANNER ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF142419)),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF00E676))
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color(0x2800E676)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF00E676),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "ADS ARE COMPLETELY REMOVED",
                            color = Color(0xFF00E676),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "You are currently enjoying an uninterrupted, 100% ad-free AbsForge experience.",
                            color = Color(0xFFB0C4B8),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    preferencesManager.setIsPremium(false)
                                    AdMobManager.setPremiumUser(false)
                                    Toast.makeText(context, "Test: Ads Re-Enabled", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFFA500)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Turn Ads Back On (Testing Mode)", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Header Hero
            Text(
                text = "TRAIN WITHOUT ADS",
                color = limeColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Zero banner ads, zero popups, zero video interruptions. Choose your plan to unlock.",
                color = Color(0xFF9AA8A0),
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Value Props
            val perks = listOf(
                "100% Ad-Free Workouts (No Banners or Popups)",
                "No Video Delays or Countdown Interruptions",
                "Instant In-App Cashfree Payment Activation",
                "Permanent Lifetime Option Available"
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF131715))
                    .padding(16.dp)
            ) {
                perks.forEach { perk ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = limeColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = perk,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "CHOOSE YOUR PLAN",
                color = Color(0xFF7A8B82),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3 Required Tiers: 1 Week ($7), 1 Month ($20), Permanent ($30)
            CashfreeConfig.PLANS.forEachIndexed { index, plan ->
                PlanSelectCard(
                    plan = plan,
                    isSelected = selectedPlanIndex == index,
                    onClick = { selectedPlanIndex = index }
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // In-App Cashfree Checkout CTA
            Button(
                onClick = {
                    showPaymentDialog = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = limeColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "PAY ${currentPlan.usdPrice} & REMOVE ADS",
                        color = Color.Black,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFF6B7A72),
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Cashfree In-App Checkout • Secure 256-Bit SSL",
                    color = Color(0xFF6B7A72),
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fast Test Button
            OutlinedButton(
                onClick = {
                    scope.launch {
                        preferencesManager.setIsPremium(true)
                        AdMobManager.setPremiumUser(true)
                        lastPurchasedPlan = currentPlan
                        showSuccessDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF888888)),
                shape = RoundedCornerShape(10.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2B332F))
                )
            ) {
                Text(
                    "⚡ Instant Test Activation (Simulate Cashfree Success)",
                    fontSize = 12.sp,
                    color = Color(0xFF9AA8A0)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // In-App Cashfree Payment WebView Dialog
    if (showPaymentDialog) {
        CashfreePaymentDialog(
            plan = currentPlan,
            onSuccess = { orderId, plan ->
                scope.launch {
                    preferencesManager.setIsPremium(true)
                    AdMobManager.setPremiumUser(true)
                    lastPurchasedPlan = plan
                    showPaymentDialog = false
                    showSuccessDialog = true
                }
            },
            onDismiss = {
                showPaymentDialog = false
            },
            onError = { error ->
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                showPaymentDialog = false
            }
        )
    }

    // Success Celebration Modal
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            containerColor = Color(0xFF141916),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎉 ADS REMOVED!", color = limeColor, fontWeight = FontWeight.Black, fontSize = 20.sp)
                }
            },
            text = {
                Column {
                    Text(
                        "Your purchase of ${lastPurchasedPlan?.title ?: currentPlan.title} (${lastPurchasedPlan?.usdPrice ?: currentPlan.usdPrice}) was successful via Cashfree Payments.",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        "All banner, interstitial, and video ads have been completely turned off throughout the entire app.",
                        color = Color(0xFF9AA8A0),
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = limeColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("START AD-FREE WORKOUT", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun PlanSelectCard(
    plan: PremiumPlan,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val limeColor = Color(0xFFB7FF00)
    val borderColor = when {
        isSelected -> limeColor
        plan.isBestValue -> Color(0xFF38463E)
        else -> Color(0xFF1F2622)
    }

    val cardBg = if (isSelected) Color(0xFF17201A) else Color(0xFF121614)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .border(if (isSelected) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Radio icon
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .border(2.dp, if (isSelected) limeColor else Color(0xFF55605A), CircleShape)
                        .background(if (isSelected) limeColor else Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Black)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = plan.title,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (plan.badge != null) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (plan.isBestValue) Color(0xFFB7FF00) else Color(0xFF243329))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = plan.badge,
                                    color = if (plan.isBestValue) Color.Black else Color(0xFF00E676),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = plan.durationText,
                        color = Color(0xFF88968E),
                        fontSize = 12.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = plan.usdPrice,
                    color = if (isSelected) limeColor else Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = plan.inrPrice,
                    color = Color(0xFF88968E),
                    fontSize = 11.sp
                )
            }
        }
    }
}
