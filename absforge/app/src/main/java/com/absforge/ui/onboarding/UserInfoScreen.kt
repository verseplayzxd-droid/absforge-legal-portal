package com.absforge.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.absforge.ui.components.AbsForgeButton
import com.absforge.ui.theme.*
import java.util.Locale

@Composable
fun UserInfoScreen(
    onNext: () -> Unit,
    viewModel: OnboardingViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val gender by viewModel.gender.collectAsState()
    val age by viewModel.age.collectAsState()
    val height by viewModel.height.collectAsState()
    val weight by viewModel.weight.collectAsState()
    val targetWeight by viewModel.targetWeight.collectAsState()

    // Provide smart sensible defaults on initial display so user never faces empty or broken state
    LaunchedEffect(Unit) {
        if (viewModel.gender.value.isBlank()) viewModel.gender.value = "Male"
        if (viewModel.age.value.isBlank()) viewModel.age.value = "25"
        if (viewModel.height.value.isBlank()) viewModel.height.value = "175"
        if (viewModel.weight.value.isBlank()) viewModel.weight.value = "70"
        if (viewModel.targetWeight.value.isBlank()) viewModel.targetWeight.value = "65"
    }

    val ageInt = age.toIntOrNull() ?: 0
    val heightInt = height.toIntOrNull() ?: 0
    val weightInt = weight.toIntOrNull() ?: 0
    val targetWeightInt = targetWeight.toIntOrNull() ?: 0

    val isAgeValid = ageInt in 14..99
    val isHeightValid = heightInt in 120..230
    val isWeightValid = weightInt in 35..220
    val isTargetWeightValid = targetWeightInt in 35..220

    val isValid = gender.isNotEmpty() && isAgeValid && isHeightValid && isWeightValid && isTargetWeightValid

    // Live BMI calculation
    val bmi = remember(heightInt, weightInt) {
        if (heightInt in 120..230 && weightInt in 35..220) {
            val hM = heightInt / 100f
            weightInt / (hM * hM)
        } else {
            null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "STEP 4 OF 5",
                    color = AbsForgePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp
                )
                Surface(
                    color = AbsForgePrimary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "BODY CALIBRATION",
                        color = AbsForgePrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your Physical Profile",
                color = AbsForgeTextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black
            )

            Text(
                text = "Helps calculate optimal core recovery intervals and target repetitions.",
                color = AbsForgeTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Gender Selection
            Text(
                text = "GENDER",
                color = AbsForgeTextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GenderChip(
                    text = "Male",
                    isSelected = gender == "Male",
                    icon = { MaleIcon(isSelected = gender == "Male") },
                    onClick = { viewModel.gender.value = "Male" },
                    modifier = Modifier.weight(1f)
                )
                GenderChip(
                    text = "Female",
                    isSelected = gender == "Female",
                    icon = { FemaleIcon(isSelected = gender == "Female") },
                    onClick = { viewModel.gender.value = "Female" },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Age Input with Stepper Controls
            ValidatedNumberField(
                label = "AGE",
                value = age,
                suffix = "years",
                rangeHint = "Valid range: 14 – 99 yrs",
                isValid = isAgeValid,
                onValueChange = { newVal ->
                    viewModel.age.value = newVal.filter { it.isDigit() }.take(2)
                },
                onStepMinus = {
                    val current = age.toIntOrNull() ?: 25
                    if (current > 14) viewModel.age.value = (current - 1).toString()
                },
                onStepPlus = {
                    val current = age.toIntOrNull() ?: 25
                    if (current < 99) viewModel.age.value = (current + 1).toString()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Height Input with Stepper Controls
            ValidatedNumberField(
                label = "HEIGHT",
                value = height,
                suffix = "cm",
                rangeHint = "Valid range: 120 – 230 cm",
                isValid = isHeightValid,
                onValueChange = { newVal ->
                    viewModel.height.value = newVal.filter { it.isDigit() }.take(3)
                },
                onStepMinus = {
                    val current = height.toIntOrNull() ?: 175
                    if (current > 120) viewModel.height.value = (current - 1).toString()
                },
                onStepPlus = {
                    val current = height.toIntOrNull() ?: 175
                    if (current < 230) viewModel.height.value = (current + 1).toString()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weight Input with Stepper Controls
            ValidatedNumberField(
                label = "CURRENT WEIGHT",
                value = weight,
                suffix = "kg",
                rangeHint = "Valid range: 35 – 220 kg",
                isValid = isWeightValid,
                onValueChange = { newVal ->
                    viewModel.weight.value = newVal.filter { it.isDigit() }.take(3)
                },
                onStepMinus = {
                    val current = weight.toIntOrNull() ?: 70
                    if (current > 35) viewModel.weight.value = (current - 1).toString()
                },
                onStepPlus = {
                    val current = weight.toIntOrNull() ?: 70
                    if (current < 220) viewModel.weight.value = (current + 1).toString()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Target Weight Input with Stepper Controls
            ValidatedNumberField(
                label = "TARGET WEIGHT",
                value = targetWeight,
                suffix = "kg",
                rangeHint = "Valid range: 35 – 220 kg",
                isValid = isTargetWeightValid,
                onValueChange = { newVal ->
                    viewModel.targetWeight.value = newVal.filter { it.isDigit() }.take(3)
                },
                onStepMinus = {
                    val current = targetWeight.toIntOrNull() ?: 65
                    if (current > 35) viewModel.targetWeight.value = (current - 1).toString()
                },
                onStepPlus = {
                    val current = targetWeight.toIntOrNull() ?: 65
                    if (current < 220) viewModel.targetWeight.value = (current + 1).toString()
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Live BMI Analysis Card
            if (bmi != null) {
                val (category, categoryColor, note) = when {
                    bmi < 18.5f -> Triple("Underweight", Color(0xFF64B5F6), "Plan will emphasize core strength and stability.")
                    bmi < 25f -> Triple("Normal Weight", AbsForgePrimary, "Ideal baseline! Optimal for muscle definition.")
                    bmi < 30f -> Triple("Overweight", Color(0xFFFFB74D), "High core calorie expenditure mode enabled.")
                    else -> Triple("High BMI", Color(0xFFFF7043), "Low-impact core progression calibrated.")
                }

                Surface(
                    color = AbsForgeSurfaceElevated,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AbsForgeGhostBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "LIVE BMI: ${String.format(Locale.US, "%.1f", bmi)}",
                                    color = AbsForgeTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = categoryColor.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = category.uppercase(),
                                        color = categoryColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = note,
                                color = AbsForgeTextSecondary,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        AbsForgeButton(
            text = "CONTINUE TO PLAN →",
            onClick = onNext,
            enabled = isValid,
            modifier = Modifier.fillMaxWidth().height(52.dp)
        )
    }
}

@Composable
fun ValidatedNumberField(
    label: String,
    value: String,
    suffix: String,
    rangeHint: String,
    isValid: Boolean,
    onValueChange: (String) -> Unit,
    onStepMinus: () -> Unit,
    onStepPlus: () -> Unit
) {
    Surface(
        color = AbsForgeSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (!isValid && value.isNotBlank()) Color(0xFFFF5252) else AbsForgeGhostBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    color = AbsForgeTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = rangeHint,
                    color = if (!isValid && value.isNotBlank()) Color(0xFFFF5252) else AbsForgeTextSecondary.copy(alpha = 0.6f),
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Stepper Minus Button
                IconButton(
                    onClick = onStepMinus,
                    modifier = Modifier
                        .size(40.dp)
                        .background(AbsForgeSurfaceElevated, CircleShape)
                        .border(1.dp, AbsForgeGhostBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = AbsForgeTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Center Value Display & Input
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = onValueChange,
                        modifier = Modifier.width(90.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = AbsForgePrimary,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedTextColor = AbsForgeTextPrimary,
                            unfocusedTextColor = AbsForgeTextPrimary,
                            cursorColor = AbsForgePrimary
                        ),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            color = AbsForgeTextPrimary
                        )
                    )
                    Text(
                        text = suffix,
                        color = AbsForgePrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Stepper Plus Button
                IconButton(
                    onClick = onStepPlus,
                    modifier = Modifier
                        .size(40.dp)
                        .background(AbsForgeSurfaceElevated, CircleShape)
                        .border(1.dp, AbsForgeGhostBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = AbsForgeTextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun GenderChip(
    text: String,
    isSelected: Boolean,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(58.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AbsForgePrimary.copy(alpha = 0.15f) else AbsForgeSurface
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) AbsForgePrimary else AbsForgeGhostBorder
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp)
        ) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                color = if (isSelected) AbsForgePrimary else AbsForgeTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
fun MaleIcon(isSelected: Boolean, modifier: Modifier = Modifier) {
    val color = if (isSelected) AbsForgePrimary else AbsForgeTextSecondary
    Canvas(modifier = modifier.size(20.dp)) {
        val strokeWidth = 2.dp.toPx()
        val center = Offset(size.width * 0.4f, size.height * 0.6f)
        val radius = size.width * 0.26f

        drawCircle(
            color = color,
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        val arrowStart = Offset(
            center.x + radius * 0.707f,
            center.y - radius * 0.707f
        )
        val arrowEnd = Offset(size.width * 0.88f, size.height * 0.12f)
        drawLine(
            color = color,
            start = arrowStart,
            end = arrowEnd,
            strokeWidth = strokeWidth
        )
        drawLine(
            color = color,
            start = arrowEnd,
            end = Offset(arrowEnd.x - size.width * 0.28f, arrowEnd.y),
            strokeWidth = strokeWidth
        )
        drawLine(
            color = color,
            start = arrowEnd,
            end = Offset(arrowEnd.x, arrowEnd.y + size.height * 0.28f),
            strokeWidth = strokeWidth
        )
    }
}

@Composable
fun FemaleIcon(isSelected: Boolean, modifier: Modifier = Modifier) {
    val color = if (isSelected) AbsForgePrimary else AbsForgeTextSecondary
    Canvas(modifier = modifier.size(20.dp)) {
        val strokeWidth = 2.dp.toPx()
        val center = Offset(size.width * 0.5f, size.height * 0.35f)
        val radius = size.width * 0.26f

        drawCircle(
            color = color,
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        val lineStart = Offset(center.x, center.y + radius)
        val lineEnd = Offset(center.x, size.height * 0.92f)
        drawLine(
            color = color,
            start = lineStart,
            end = lineEnd,
            strokeWidth = strokeWidth
        )

        val crossY = center.y + radius + (lineEnd.y - lineStart.y) * 0.45f
        val crossArm = size.width * 0.22f
        drawLine(
            color = color,
            start = Offset(center.x - crossArm, crossY),
            end = Offset(center.x + crossArm, crossY),
            strokeWidth = strokeWidth
        )
    }
}
