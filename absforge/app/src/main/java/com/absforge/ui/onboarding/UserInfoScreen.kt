package com.absforge.ui.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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

    val isValid = gender.isNotEmpty() && age.isNotEmpty() && height.isNotEmpty() && weight.isNotEmpty() && targetWeight.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbsForgeBackground)
            .padding(24.dp)
            .padding(top = 40.dp)
    ) {
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            Text(
                text = "4 of 5",
                color = AbsForgePrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tell us about yourself",
                color = AbsForgeTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text("Gender", color = AbsForgeTextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
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

            Spacer(modifier = Modifier.height(24.dp))
            AbsForgeTextField(
                label = "Age",
                value = age,
                onValueChange = { viewModel.age.value = it },
                suffix = "years"
            )

            Spacer(modifier = Modifier.height(24.dp))
            AbsForgeTextField(
                label = "Height",
                value = height,
                onValueChange = { viewModel.height.value = it },
                suffix = "cm"
            )

            Spacer(modifier = Modifier.height(24.dp))
            AbsForgeTextField(
                label = "Weight",
                value = weight,
                onValueChange = { viewModel.weight.value = it },
                suffix = "kg"
            )

            Spacer(modifier = Modifier.height(24.dp))
            AbsForgeTextField(
                label = "Target Weight",
                value = targetWeight,
                onValueChange = { viewModel.targetWeight.value = it },
                suffix = "kg"
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        AbsForgeButton(
            text = "NEXT",
            onClick = onNext,
            enabled = isValid,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
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
            .height(64.dp)
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
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            icon()
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                color = if (isSelected) AbsForgePrimary else AbsForgeTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun MaleIcon(isSelected: Boolean, modifier: Modifier = Modifier) {
    val color = if (isSelected) AbsForgePrimary else AbsForgeTextSecondary
    Canvas(modifier = modifier.size(22.dp)) {
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
    Canvas(modifier = modifier.size(22.dp)) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsForgeTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String
) {
    Column {
        Text(label, color = AbsForgeTextSecondary, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = AbsForgePrimary,
                unfocusedIndicatorColor = AbsForgeGhostBorder,
                focusedContainerColor = AbsForgeSurface,
                unfocusedContainerColor = AbsForgeSurface,
                focusedTextColor = AbsForgeTextPrimary,
                unfocusedTextColor = AbsForgeTextPrimary,
                cursorColor = AbsForgePrimary
            ),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            trailingIcon = {
                Text(suffix, color = AbsForgeTextSecondary, modifier = Modifier.padding(end = 16.dp))
            }
        )
    }
}
