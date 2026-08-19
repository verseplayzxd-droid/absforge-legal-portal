package com.absforge.ui.animation

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.util.lerp
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class BodyPose(
    val headOffset: Offset = Offset(0f, -0.35f),
    val shoulderLeft: Offset,
    val shoulderRight: Offset,
    val elbowLeft: Offset,
    val elbowRight: Offset,
    val handLeft: Offset,
    val handRight: Offset,
    val hipLeft: Offset,
    val hipRight: Offset,
    val kneeLeft: Offset,
    val kneeRight: Offset,
    val footLeft: Offset,
    val footRight: Offset,
    val torsoTop: Offset,
    val torsoBottom: Offset,
    val highlightedMuscles: List<MuscleHighlight> = emptyList()
)

data class MuscleHighlight(
    val position: Offset,
    val radius: Float = 0.05f,
    val color: Color = Color(0xFFB7FF00).copy(alpha = 0.6f)
)

/**
 * Draws a professional, articulated 2D athletic human figure with anatomical proportions.
 */
fun DrawScope.drawHumanFigure(
    pose: BodyPose,
    canvasCenter: Offset,
    scale: Float,
    bodyColor: Color = Color(0xFFE2E8F0),
    accentColor: Color = Color(0xFF94A3B8),
    highlightColor: Color = Color(0xFFB7FF00)
) {
    fun Offset.toCanvas(): Offset {
        return Offset(
            x = canvasCenter.x + this.x * scale,
            y = canvasCenter.y + this.y * scale
        )
    }

    val headCenter = pose.headOffset.toCanvas()
    val torsoTop = pose.torsoTop.toCanvas()
    val torsoBottom = pose.torsoBottom.toCanvas()
    val shoulderL = pose.shoulderLeft.toCanvas()
    val shoulderR = pose.shoulderRight.toCanvas()
    val elbowL = pose.elbowLeft.toCanvas()
    val elbowR = pose.elbowRight.toCanvas()
    val handL = pose.handLeft.toCanvas()
    val handR = pose.handRight.toCanvas()
    val hipL = pose.hipLeft.toCanvas()
    val hipR = pose.hipRight.toCanvas()
    val kneeL = pose.kneeLeft.toCanvas()
    val kneeR = pose.kneeRight.toCanvas()
    val footL = pose.footLeft.toCanvas()
    val footR = pose.footRight.toCanvas()

    val baseWidth = scale * 0.045f
    val headRadius = scale * 0.075f

    // Helper to draw realistic limb segments (capsule/tapered polygon)
    fun drawLimbSegment(p1: Offset, p2: Offset, startWidth: Float, endWidth: Float, color: Color) {
        val dx = p2.x - p1.x
        val dy = p2.y - p1.y
        val angle = atan2(dy, dx)
        val nx = -sin(angle)
        val ny = cos(angle)

        val path = Path().apply {
            moveTo(p1.x + nx * startWidth, p1.y + ny * startWidth)
            lineTo(p2.x + nx * endWidth, p2.y + ny * endWidth)
            lineTo(p2.x - nx * endWidth, p2.y - ny * endWidth)
            lineTo(p1.x - nx * startWidth, p1.y - ny * startWidth)
            close()
        }
        drawPath(path, color)

        // Smooth rounded joints at endpoints
        drawCircle(color, radius = startWidth, center = p1)
        drawCircle(color, radius = endWidth, center = p2)
    }

    // 1. Draw Legs (Back leg first, then front leg for depth)
    drawLimbSegment(hipR, kneeR, baseWidth * 1.3f, baseWidth * 1.1f, accentColor)
    drawLimbSegment(kneeR, footR, baseWidth * 1.1f, baseWidth * 0.8f, accentColor)

    drawLimbSegment(hipL, kneeL, baseWidth * 1.35f, baseWidth * 1.15f, bodyColor)
    drawLimbSegment(kneeL, footL, baseWidth * 1.15f, baseWidth * 0.85f, bodyColor)

    // 2. Draw Torso (Athletic V-Tapered Upper Body)
    val torsoPath = Path().apply {
        moveTo(shoulderL.x, shoulderL.y)
        lineTo(shoulderR.x, shoulderR.y)
        lineTo(hipR.x, hipR.y)
        lineTo(hipL.x, hipL.y)
        close()
    }
    drawPath(torsoPath, bodyColor)

    // Smooth rounded shoulders & hips
    drawCircle(bodyColor, radius = baseWidth * 1.2f, center = shoulderL)
    drawCircle(bodyColor, radius = baseWidth * 1.2f, center = shoulderR)
    drawCircle(bodyColor, radius = baseWidth * 1.1f, center = hipL)
    drawCircle(bodyColor, radius = baseWidth * 1.1f, center = hipR)

    // 3. Target Muscle Highlight (Abdominal Focus)
    pose.highlightedMuscles.forEach { highlight ->
        val pos = highlight.position.toCanvas()
        drawCircle(
            color = highlightColor.copy(alpha = 0.25f),
            radius = highlight.radius * scale * 1.8f,
            center = pos
        )
        drawCircle(
            color = highlightColor.copy(alpha = 0.7f),
            radius = highlight.radius * scale * 0.9f,
            center = pos
        )
        // Core muscle stroke detail
        drawCircle(
            color = Color(0xFF090B0A),
            radius = highlight.radius * scale * 0.9f,
            center = pos,
            style = Stroke(width = scale * 0.008f)
        )
    }

    // 4. Draw Arms (Back arm first, then front arm)
    drawLimbSegment(shoulderR, elbowR, baseWidth * 1.1f, baseWidth * 0.9f, accentColor)
    drawLimbSegment(elbowR, handR, baseWidth * 0.9f, baseWidth * 0.7f, accentColor)

    drawLimbSegment(shoulderL, elbowL, baseWidth * 1.15f, baseWidth * 0.95f, bodyColor)
    drawLimbSegment(elbowL, handL, baseWidth * 0.95f, baseWidth * 0.75f, bodyColor)

    // 5. Draw Neck & Head
    val neckWidth = baseWidth * 0.8f
    val neckPath = Path().apply {
        moveTo(torsoTop.x - neckWidth, torsoTop.y)
        lineTo(torsoTop.x + neckWidth, torsoTop.y)
        lineTo(headCenter.x + neckWidth * 0.6f, headCenter.y + headRadius * 0.5f)
        lineTo(headCenter.x - neckWidth * 0.6f, headCenter.y + headRadius * 0.5f)
        close()
    }
    drawPath(neckPath, bodyColor)

    drawCircle(color = bodyColor, radius = headRadius, center = headCenter)
    // Head accent rim for 3D depth
    drawCircle(
        color = Color(0xFF090B0A).copy(alpha = 0.2f),
        radius = headRadius,
        center = headCenter,
        style = Stroke(width = scale * 0.012f)
    )
}

fun interpolatePose(start: BodyPose, end: BodyPose, progress: Float): BodyPose {
    fun lerpOffset(start: Offset, end: Offset, fraction: Float): Offset {
        return Offset(
            lerp(start.x, end.x, fraction),
            lerp(start.y, end.y, fraction)
        )
    }

    val highlights = if (start.highlightedMuscles.size == end.highlightedMuscles.size) {
        start.highlightedMuscles.mapIndexed { index, m1 ->
            val m2 = end.highlightedMuscles[index]
            MuscleHighlight(
                position = lerpOffset(m1.position, m2.position, progress),
                radius = lerp(m1.radius, m2.radius, progress),
                color = m1.color
            )
        }
    } else {
        if (progress < 0.5f) start.highlightedMuscles else end.highlightedMuscles
    }

    return BodyPose(
        headOffset = lerpOffset(start.headOffset, end.headOffset, progress),
        shoulderLeft = lerpOffset(start.shoulderLeft, end.shoulderLeft, progress),
        shoulderRight = lerpOffset(start.shoulderRight, end.shoulderRight, progress),
        elbowLeft = lerpOffset(start.elbowLeft, end.elbowLeft, progress),
        elbowRight = lerpOffset(start.elbowRight, end.elbowRight, progress),
        handLeft = lerpOffset(start.handLeft, end.handLeft, progress),
        handRight = lerpOffset(start.handRight, end.handRight, progress),
        hipLeft = lerpOffset(start.hipLeft, end.hipLeft, progress),
        hipRight = lerpOffset(start.hipRight, end.hipRight, progress),
        kneeLeft = lerpOffset(start.kneeLeft, end.kneeLeft, progress),
        kneeRight = lerpOffset(start.kneeRight, end.kneeRight, progress),
        footLeft = lerpOffset(start.footLeft, end.footLeft, progress),
        footRight = lerpOffset(start.footRight, end.footRight, progress),
        torsoTop = lerpOffset(start.torsoTop, end.torsoTop, progress),
        torsoBottom = lerpOffset(start.torsoBottom, end.torsoBottom, progress),
        highlightedMuscles = highlights
    )
}
