package com.absforge.ui.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumScreen(onBack: () -> Unit) {
    val bgColor = Color(0xFF090B0A)
    val surfaceColor = Color(0xFF141715)
    val limeColor = Color(0xFFB7FF00)
    val context = LocalContext.current

    var selectedPlan by remember { mutableIntStateOf(1) } // 0: Monthly, 1: Yearly, 2: Lifetime

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AbsForge Pro", color = Color.White) },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "TRAIN WITHOUT DISTRACTIONS",
                color = limeColor,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            
            val benefits = listOf("No Ads", "Advanced Voice Coach", "All Workout Plans", "Detailed Analytics")
            benefits.forEach { benefit ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = limeColor)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(benefit, color = Color.White, fontSize = 16.sp)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PlanCard("Monthly", "$4.99 / mo", selectedPlan == 0) { selectedPlan = 0 }
            Spacer(modifier = Modifier.height(12.dp))
            PlanCard("Yearly (Most Popular)", "$24.99 / yr", selectedPlan == 1) { selectedPlan = 1 }
            Spacer(modifier = Modifier.height(12.dp))
            PlanCard("Lifetime", "$49.99 once", selectedPlan == 2) { selectedPlan = 2 }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = limeColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("UPGRADE TO PRO", color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(onClick = { Toast.makeText(context, "Coming soon", Toast.LENGTH_SHORT).show() }) {
                Text("RESTORE PURCHASE", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun PlanCard(title: String, price: String, isSelected: Boolean, onClick: () -> Unit) {
    val limeColor = Color(0xFFB7FF00)
    val surfaceColor = Color(0xFF141715)
    
    val modifier = Modifier
        .fillMaxWidth()
        .background(surfaceColor, RoundedCornerShape(12.dp))
        .clickable(onClick = onClick)
        .run {
            if (isSelected) border(2.dp, limeColor, RoundedCornerShape(12.dp)) else this
        }
        
    Row(
        modifier = modifier.padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(price, color = if (isSelected) limeColor else Color.White, fontSize = 16.sp)
    }
}
