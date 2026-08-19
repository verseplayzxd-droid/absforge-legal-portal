package com.absforge.ui.animation

import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.geometry.Offset

data class ExerciseAnimationDef(
    val startPose: BodyPose,
    val endPose: BodyPose,
    val repeatMode: RepeatMode = RepeatMode.Reverse,
    val durationMs: Int = 2000
)

object ExerciseAnimations {
    
    private val defaultLying = BodyPose(
        headOffset = Offset(-0.35f, 0f),
        torsoTop = Offset(-0.25f, 0f),
        torsoBottom = Offset(0.05f, 0f),
        shoulderLeft = Offset(-0.25f, -0.05f),
        shoulderRight = Offset(-0.25f, 0.05f),
        elbowLeft = Offset(-0.05f, -0.05f),
        elbowRight = Offset(-0.05f, 0.05f),
        handLeft = Offset(0.15f, -0.05f),
        handRight = Offset(0.15f, 0.05f),
        hipLeft = Offset(0.05f, -0.04f),
        hipRight = Offset(0.05f, 0.04f),
        kneeLeft = Offset(0.25f, -0.04f),
        kneeRight = Offset(0.25f, 0.04f),
        footLeft = Offset(0.45f, -0.04f),
        footRight = Offset(0.45f, 0.04f),
        highlightedMuscles = listOf(MuscleHighlight(Offset(-0.1f, 0f)))
    )

    private val defaultStanding = BodyPose(
        headOffset = Offset(0f, -0.35f),
        torsoTop = Offset(0f, -0.25f),
        torsoBottom = Offset(0f, 0.05f),
        shoulderLeft = Offset(-0.05f, -0.25f),
        shoulderRight = Offset(0.05f, -0.25f),
        elbowLeft = Offset(-0.08f, -0.05f),
        elbowRight = Offset(0.08f, -0.05f),
        handLeft = Offset(-0.08f, 0.15f),
        handRight = Offset(0.08f, 0.15f),
        hipLeft = Offset(-0.04f, 0.05f),
        hipRight = Offset(0.04f, 0.05f),
        kneeLeft = Offset(-0.04f, 0.25f),
        kneeRight = Offset(0.04f, 0.25f),
        footLeft = Offset(-0.04f, 0.45f),
        footRight = Offset(0.04f, 0.45f),
        highlightedMuscles = listOf(MuscleHighlight(Offset(0f, -0.1f)))
    )

    private val defaultPlank = BodyPose(
        headOffset = Offset(-0.35f, 0.1f),
        torsoTop = Offset(-0.25f, 0.15f),
        torsoBottom = Offset(0.1f, 0.15f),
        shoulderLeft = Offset(-0.25f, 0.15f),
        shoulderRight = Offset(-0.25f, 0.15f),
        elbowLeft = Offset(-0.25f, 0.35f),
        elbowRight = Offset(-0.25f, 0.35f),
        handLeft = Offset(-0.1f, 0.35f),
        handRight = Offset(-0.1f, 0.35f),
        hipLeft = Offset(0.1f, 0.15f),
        hipRight = Offset(0.1f, 0.15f),
        kneeLeft = Offset(0.3f, 0.25f),
        kneeRight = Offset(0.3f, 0.25f),
        footLeft = Offset(0.45f, 0.35f),
        footRight = Offset(0.45f, 0.35f),
        highlightedMuscles = listOf(MuscleHighlight(Offset(-0.05f, 0.15f)))
    )
    
    private val defaultSeated = BodyPose(
        headOffset = Offset(-0.15f, -0.25f),
        torsoTop = Offset(-0.1f, -0.15f),
        torsoBottom = Offset(0.05f, 0.1f),
        shoulderLeft = Offset(-0.1f, -0.15f),
        shoulderRight = Offset(-0.1f, -0.15f),
        elbowLeft = Offset(0.05f, -0.05f),
        elbowRight = Offset(0.05f, -0.05f),
        handLeft = Offset(0.2f, -0.05f),
        handRight = Offset(0.2f, -0.05f),
        hipLeft = Offset(0.05f, 0.1f),
        hipRight = Offset(0.05f, 0.1f),
        kneeLeft = Offset(0.25f, 0f),
        kneeRight = Offset(0.25f, 0f),
        footLeft = Offset(0.45f, 0.1f),
        footRight = Offset(0.45f, 0.1f),
        highlightedMuscles = listOf(MuscleHighlight(Offset(-0.02f, -0.02f)))
    )

    private val exercises = mapOf(
        "crunch" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                elbowLeft = Offset(-0.35f, -0.15f), elbowRight = Offset(-0.35f, 0.15f),
                handLeft = Offset(-0.35f, 0f), handRight = Offset(-0.35f, 0f),
                kneeLeft = Offset(0.15f, -0.15f), kneeRight = Offset(0.15f, 0.15f),
                footLeft = Offset(0.25f, 0.1f), footRight = Offset(0.25f, 0.1f)
            ),
            endPose = defaultLying.copy(
                headOffset = Offset(-0.25f, -0.15f),
                torsoTop = Offset(-0.15f, -0.1f),
                elbowLeft = Offset(-0.25f, -0.25f), elbowRight = Offset(-0.25f, 0.15f),
                handLeft = Offset(-0.25f, -0.1f), handRight = Offset(-0.25f, -0.1f),
                kneeLeft = Offset(0.15f, -0.15f), kneeRight = Offset(0.15f, 0.15f),
                footLeft = Offset(0.25f, 0.1f), footRight = Offset(0.25f, 0.1f),
                highlightedMuscles = listOf(MuscleHighlight(Offset(-0.05f, -0.05f), 0.06f))
            )
        ),
        "reverse_crunch" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                kneeLeft = Offset(0.2f, -0.15f), kneeRight = Offset(0.2f, -0.15f),
                footLeft = Offset(0.35f, -0.1f), footRight = Offset(0.35f, -0.1f)
            ),
            endPose = defaultLying.copy(
                hipLeft = Offset(0.0f, -0.1f), hipRight = Offset(0.0f, -0.1f),
                kneeLeft = Offset(-0.1f, -0.2f), kneeRight = Offset(-0.1f, -0.2f),
                footLeft = Offset(0.05f, -0.3f), footRight = Offset(0.05f, -0.3f),
                torsoBottom = Offset(0.0f, -0.1f),
                highlightedMuscles = listOf(MuscleHighlight(Offset(-0.05f, -0.05f), 0.06f))
            )
        ),
        "bicycle_crunch" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                headOffset = Offset(-0.25f, -0.15f), torsoTop = Offset(-0.15f, -0.1f),
                elbowLeft = Offset(-0.1f, -0.15f), elbowRight = Offset(-0.35f, 0.15f),
                kneeLeft = Offset(-0.1f, -0.15f), kneeRight = Offset(0.25f, 0.1f),
                footLeft = Offset(0.05f, 0f), footRight = Offset(0.45f, 0.1f)
            ),
            endPose = defaultLying.copy(
                headOffset = Offset(-0.25f, 0.15f), torsoTop = Offset(-0.15f, 0.1f),
                elbowLeft = Offset(-0.35f, -0.15f), elbowRight = Offset(-0.1f, 0.15f),
                kneeLeft = Offset(0.25f, -0.1f), kneeRight = Offset(-0.1f, 0.15f),
                footLeft = Offset(0.45f, -0.1f), footRight = Offset(0.05f, 0f)
            )
        ),
        "v_crunch" to ExerciseAnimationDef(
            startPose = defaultLying,
            endPose = defaultLying.copy(
                headOffset = Offset(-0.1f, -0.25f), torsoTop = Offset(-0.05f, -0.15f),
                shoulderLeft = Offset(-0.05f, -0.15f), shoulderRight = Offset(-0.05f, -0.15f),
                handLeft = Offset(0.15f, -0.2f), handRight = Offset(0.15f, -0.2f),
                elbowLeft = Offset(0.05f, -0.15f), elbowRight = Offset(0.05f, -0.15f),
                hipLeft = Offset(0.05f, 0.05f), hipRight = Offset(0.05f, 0.05f),
                kneeLeft = Offset(0.1f, -0.15f), kneeRight = Offset(0.1f, -0.15f),
                footLeft = Offset(0.15f, -0.3f), footRight = Offset(0.15f, -0.3f)
            )
        ),
        "v_up" to ExerciseAnimationDef(
            startPose = defaultLying,
            endPose = defaultLying.copy(
                headOffset = Offset(-0.05f, -0.25f), torsoTop = Offset(0f, -0.15f),
                shoulderLeft = Offset(0f, -0.15f), shoulderRight = Offset(0f, -0.15f),
                handLeft = Offset(0.1f, -0.35f), handRight = Offset(0.1f, -0.35f),
                elbowLeft = Offset(0.05f, -0.25f), elbowRight = Offset(0.05f, -0.25f),
                kneeLeft = Offset(0.05f, -0.15f), kneeRight = Offset(0.05f, -0.15f),
                footLeft = Offset(0.1f, -0.35f), footRight = Offset(0.1f, -0.35f)
            )
        ),
        "leg_raise" to ExerciseAnimationDef(
            startPose = defaultLying,
            endPose = defaultLying.copy(
                kneeLeft = Offset(0.05f, -0.2f), kneeRight = Offset(0.05f, -0.2f),
                footLeft = Offset(0.05f, -0.4f), footRight = Offset(0.05f, -0.4f),
                highlightedMuscles = listOf(MuscleHighlight(Offset(0.0f, 0f), 0.06f))
            )
        ),
        "bent_leg_twist" to ExerciseAnimationDef(
            startPose = defaultSeated.copy(
                shoulderLeft = Offset(-0.1f, -0.2f), elbowLeft = Offset(0.05f, -0.15f), handLeft = Offset(0.2f, -0.1f),
                shoulderRight = Offset(-0.15f, -0.1f), elbowRight = Offset(-0.05f, 0f), handRight = Offset(0.1f, 0.05f)
            ),
            endPose = defaultSeated.copy(
                shoulderLeft = Offset(-0.15f, -0.1f), elbowLeft = Offset(-0.05f, 0f), handLeft = Offset(0.1f, 0.05f),
                shoulderRight = Offset(-0.1f, -0.2f), elbowRight = Offset(0.05f, -0.15f), handRight = Offset(0.2f, -0.1f)
            )
        ),
        "flutter_kicks" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                kneeLeft = Offset(0.2f, -0.1f), footLeft = Offset(0.4f, -0.2f),
                kneeRight = Offset(0.25f, -0.04f), footRight = Offset(0.45f, -0.04f)
            ),
            endPose = defaultLying.copy(
                kneeLeft = Offset(0.25f, -0.04f), footLeft = Offset(0.45f, -0.04f),
                kneeRight = Offset(0.2f, -0.1f), footRight = Offset(0.4f, -0.2f)
            ),
            repeatMode = RepeatMode.Restart,
            durationMs = 1200
        ),
        "heel_touch" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                kneeLeft = Offset(0.15f, -0.15f), kneeRight = Offset(0.15f, 0.15f),
                footLeft = Offset(0.25f, 0.1f), footRight = Offset(0.25f, 0.1f),
                handLeft = Offset(0.25f, -0.15f), handRight = Offset(0.0f, 0.05f),
                headOffset = Offset(-0.25f, -0.15f), torsoTop = Offset(-0.15f, -0.1f)
            ),
            endPose = defaultLying.copy(
                kneeLeft = Offset(0.15f, -0.15f), kneeRight = Offset(0.15f, 0.15f),
                footLeft = Offset(0.25f, 0.1f), footRight = Offset(0.25f, 0.1f),
                handLeft = Offset(0.0f, -0.05f), handRight = Offset(0.25f, 0.15f),
                headOffset = Offset(-0.25f, 0.15f), torsoTop = Offset(-0.15f, 0.1f)
            )
        ),
        "russian_twist" to ExerciseAnimationDef(
            startPose = defaultSeated.copy(
                handLeft = Offset(0.1f, -0.2f), handRight = Offset(0.1f, -0.2f),
                elbowLeft = Offset(0.0f, -0.15f), elbowRight = Offset(0.0f, -0.15f),
                footLeft = Offset(0.4f, -0.05f), footRight = Offset(0.4f, -0.05f)
            ),
            endPose = defaultSeated.copy(
                handLeft = Offset(0.1f, 0.2f), handRight = Offset(0.1f, 0.2f),
                elbowLeft = Offset(0.0f, 0.15f), elbowRight = Offset(0.0f, 0.15f),
                footLeft = Offset(0.4f, -0.05f), footRight = Offset(0.4f, -0.05f)
            )
        ),
        "sit_up_twist" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                kneeLeft = Offset(0.15f, -0.15f), kneeRight = Offset(0.15f, 0.15f),
                footLeft = Offset(0.25f, 0.1f), footRight = Offset(0.25f, 0.1f)
            ),
            endPose = defaultSeated.copy(
                headOffset = Offset(0f, -0.3f), torsoTop = Offset(0.05f, -0.2f),
                handLeft = Offset(0.15f, -0.15f), elbowRight = Offset(0.25f, 0.1f),
                kneeLeft = Offset(0.15f, -0.15f), kneeRight = Offset(0.15f, 0.15f),
                footLeft = Offset(0.25f, 0.1f), footRight = Offset(0.25f, 0.1f)
            )
        ),
        "mountain_climbers" to ExerciseAnimationDef(
            startPose = defaultPlank.copy(
                kneeLeft = Offset(0.1f, 0.25f), footLeft = Offset(0.15f, 0.3f)
            ),
            endPose = defaultPlank.copy(
                kneeRight = Offset(0.1f, 0.25f), footRight = Offset(0.15f, 0.3f)
            ),
            durationMs = 1000
        ),
        "plank" to ExerciseAnimationDef(
            startPose = defaultPlank,
            endPose = defaultPlank.copy(
                torsoTop = Offset(-0.25f, 0.13f),
                torsoBottom = Offset(0.1f, 0.13f)
            ),
            durationMs = 3000
        ),
        "side_plank_left" to ExerciseAnimationDef(
            startPose = defaultPlank.copy(
                shoulderRight = Offset(-0.25f, 0.05f), handRight = Offset(-0.05f, 0.05f), elbowRight = Offset(-0.15f, 0.05f),
                footLeft = Offset(0.45f, 0.35f), footRight = Offset(0.45f, 0.25f)
            ),
            endPose = defaultPlank.copy(
                shoulderRight = Offset(-0.25f, 0.05f), handRight = Offset(-0.05f, 0.05f), elbowRight = Offset(-0.15f, 0.05f),
                footLeft = Offset(0.45f, 0.35f), footRight = Offset(0.45f, 0.25f),
                torsoTop = Offset(-0.25f, 0.13f), torsoBottom = Offset(0.1f, 0.13f)
            ),
            durationMs = 3000
        ),
        "side_plank_right" to ExerciseAnimationDef(
            startPose = defaultPlank.copy(
                shoulderLeft = Offset(-0.25f, 0.05f), handLeft = Offset(-0.05f, 0.05f), elbowLeft = Offset(-0.15f, 0.05f),
                footLeft = Offset(0.45f, 0.25f), footRight = Offset(0.45f, 0.35f)
            ),
            endPose = defaultPlank.copy(
                shoulderLeft = Offset(-0.25f, 0.05f), handLeft = Offset(-0.05f, 0.05f), elbowLeft = Offset(-0.15f, 0.05f),
                footLeft = Offset(0.45f, 0.25f), footRight = Offset(0.45f, 0.35f),
                torsoTop = Offset(-0.25f, 0.13f), torsoBottom = Offset(0.1f, 0.13f)
            ),
            durationMs = 3000
        ),
        "single_leg_drops" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                kneeLeft = Offset(0.05f, -0.2f), footLeft = Offset(0.05f, -0.4f),
                kneeRight = Offset(0.05f, -0.2f), footRight = Offset(0.05f, -0.4f)
            ),
            endPose = defaultLying.copy(
                kneeLeft = Offset(0.05f, -0.2f), footLeft = Offset(0.05f, -0.4f),
                kneeRight = Offset(0.25f, -0.04f), footRight = Offset(0.45f, -0.04f)
            )
        ),
        "seated_abs_circles_cw" to ExerciseAnimationDef(
            startPose = defaultSeated.copy(
                headOffset = Offset(-0.1f, -0.3f), torsoTop = Offset(-0.05f, -0.2f)
            ),
            endPose = defaultSeated.copy(
                headOffset = Offset(-0.2f, -0.2f), torsoTop = Offset(-0.15f, -0.1f)
            )
        ),
        "seated_abs_circles_ccw" to ExerciseAnimationDef(
            startPose = defaultSeated.copy(
                headOffset = Offset(-0.2f, -0.2f), torsoTop = Offset(-0.15f, -0.1f)
            ),
            endPose = defaultSeated.copy(
                headOffset = Offset(-0.1f, -0.3f), torsoTop = Offset(-0.05f, -0.2f)
            )
        ),
        "dead_bug" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                kneeLeft = Offset(0.05f, -0.2f), footLeft = Offset(0.2f, -0.2f),
                kneeRight = Offset(0.05f, -0.2f), footRight = Offset(0.2f, -0.2f),
                handLeft = Offset(-0.25f, -0.2f), handRight = Offset(-0.25f, -0.2f)
            ),
            endPose = defaultLying.copy(
                kneeLeft = Offset(0.05f, -0.2f), footLeft = Offset(0.2f, -0.2f),
                kneeRight = Offset(0.25f, -0.04f), footRight = Offset(0.45f, -0.04f),
                handLeft = Offset(-0.25f, -0.2f), handRight = Offset(0.15f, 0.05f)
            )
        ),
        "bird_dog" to ExerciseAnimationDef(
            startPose = defaultPlank.copy(
                kneeLeft = Offset(0.1f, 0.35f), kneeRight = Offset(0.1f, 0.35f)
            ),
            endPose = defaultPlank.copy(
                handRight = Offset(-0.45f, 0.1f), elbowRight = Offset(-0.35f, 0.1f), shoulderRight = Offset(-0.25f, 0.1f),
                footLeft = Offset(0.45f, 0.1f), kneeLeft = Offset(0.3f, 0.1f), hipLeft = Offset(0.1f, 0.1f)
            )
        ),
        "toe_touch" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                kneeLeft = Offset(0.05f, -0.2f), footLeft = Offset(0.05f, -0.4f),
                kneeRight = Offset(0.05f, -0.2f), footRight = Offset(0.05f, -0.4f)
            ),
            endPose = defaultLying.copy(
                headOffset = Offset(-0.15f, -0.15f), torsoTop = Offset(-0.1f, -0.1f),
                kneeLeft = Offset(0.05f, -0.2f), footLeft = Offset(0.05f, -0.4f),
                kneeRight = Offset(0.05f, -0.2f), footRight = Offset(0.05f, -0.4f),
                handLeft = Offset(0.05f, -0.3f), handRight = Offset(0.05f, -0.3f)
            )
        ),
        "knee_to_chest_crunch" to ExerciseAnimationDef(
            startPose = defaultLying,
            endPose = defaultLying.copy(
                headOffset = Offset(-0.2f, -0.1f), torsoTop = Offset(-0.1f, -0.05f),
                kneeLeft = Offset(0.0f, -0.1f), footLeft = Offset(0.1f, -0.05f),
                kneeRight = Offset(0.0f, -0.1f), footRight = Offset(0.1f, -0.05f),
                handLeft = Offset(0.05f, -0.1f), handRight = Offset(0.05f, -0.1f)
            )
        ),
        "long_arm_crunch" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                handLeft = Offset(-0.45f, 0f), handRight = Offset(-0.45f, 0f),
                elbowLeft = Offset(-0.35f, 0f), elbowRight = Offset(-0.35f, 0f)
            ),
            endPose = defaultLying.copy(
                headOffset = Offset(-0.2f, -0.15f), torsoTop = Offset(-0.1f, -0.1f),
                handLeft = Offset(-0.1f, -0.2f), handRight = Offset(-0.1f, -0.2f),
                elbowLeft = Offset(-0.15f, -0.15f), elbowRight = Offset(-0.15f, -0.15f)
            )
        ),
        "cross_arm_crunch" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                handLeft = Offset(-0.2f, 0.05f), handRight = Offset(-0.2f, -0.05f),
                elbowLeft = Offset(-0.15f, 0.1f), elbowRight = Offset(-0.15f, -0.1f)
            ),
            endPose = defaultLying.copy(
                headOffset = Offset(-0.2f, -0.15f), torsoTop = Offset(-0.1f, -0.1f),
                handLeft = Offset(-0.1f, 0.05f), handRight = Offset(-0.1f, -0.05f),
                elbowLeft = Offset(-0.05f, 0.1f), elbowRight = Offset(-0.05f, -0.1f)
            )
        ),
        "oblique_crunch" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                kneeLeft = Offset(0.15f, 0.1f), footLeft = Offset(0.25f, 0.15f),
                kneeRight = Offset(0.15f, 0.1f), footRight = Offset(0.25f, 0.15f)
            ),
            endPose = defaultLying.copy(
                headOffset = Offset(-0.2f, -0.1f), torsoTop = Offset(-0.1f, -0.05f),
                kneeLeft = Offset(0.15f, 0.1f), footLeft = Offset(0.25f, 0.15f),
                kneeRight = Offset(0.15f, 0.1f), footRight = Offset(0.25f, 0.15f)
            )
        ),
        "leg_in_and_out" to ExerciseAnimationDef(
            startPose = defaultSeated.copy(
                kneeLeft = Offset(0.15f, 0.0f), footLeft = Offset(0.25f, 0.1f),
                kneeRight = Offset(0.15f, 0.0f), footRight = Offset(0.25f, 0.1f)
            ),
            endPose = defaultSeated.copy(
                kneeLeft = Offset(0.35f, 0.0f), footLeft = Offset(0.45f, 0.1f),
                kneeRight = Offset(0.35f, 0.0f), footRight = Offset(0.45f, 0.1f),
                torsoTop = Offset(-0.15f, -0.1f), headOffset = Offset(-0.2f, -0.2f)
            )
        ),
        "scissor_kicks" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                footLeft = Offset(0.45f, -0.1f), footRight = Offset(0.45f, 0.0f)
            ),
            endPose = defaultLying.copy(
                footLeft = Offset(0.45f, 0.0f), footRight = Offset(0.45f, -0.1f)
            )
        ),
        "high_knees" to ExerciseAnimationDef(
            startPose = defaultStanding.copy(
                kneeLeft = Offset(-0.04f, 0.1f), footLeft = Offset(-0.04f, 0.25f)
            ),
            endPose = defaultStanding.copy(
                kneeRight = Offset(0.04f, 0.1f), footRight = Offset(0.04f, 0.25f)
            ),
            durationMs = 800,
            repeatMode = RepeatMode.Restart
        ),
        "standing_bicycle_crunch" to ExerciseAnimationDef(
            startPose = defaultStanding.copy(
                handLeft = Offset(-0.08f, -0.3f), handRight = Offset(0.08f, -0.3f),
                elbowLeft = Offset(-0.15f, -0.2f), elbowRight = Offset(0.15f, -0.2f)
            ),
            endPose = defaultStanding.copy(
                handLeft = Offset(-0.08f, -0.3f), handRight = Offset(0.08f, -0.3f),
                elbowLeft = Offset(0.05f, -0.1f), elbowRight = Offset(0.15f, -0.2f),
                kneeRight = Offset(0.05f, 0.0f), footRight = Offset(0.05f, 0.15f),
                torsoTop = Offset(0.02f, -0.2f), headOffset = Offset(0.02f, -0.3f)
            )
        ),
        "cobra_stretch" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                headOffset = Offset(-0.35f, 0.1f), torsoTop = Offset(-0.25f, 0.15f), torsoBottom = Offset(0.05f, 0.15f),
                shoulderLeft = Offset(-0.25f, 0.15f), elbowLeft = Offset(-0.15f, 0.25f), handLeft = Offset(-0.2f, 0.35f),
                hipLeft = Offset(0.05f, 0.15f), kneeLeft = Offset(0.25f, 0.15f), footLeft = Offset(0.45f, 0.15f),
                hipRight = Offset(0.05f, 0.15f), kneeRight = Offset(0.25f, 0.15f), footRight = Offset(0.45f, 0.15f)
            ),
            endPose = defaultLying.copy(
                headOffset = Offset(-0.35f, -0.1f), torsoTop = Offset(-0.25f, -0.05f), torsoBottom = Offset(0.05f, 0.15f),
                shoulderLeft = Offset(-0.25f, -0.05f), elbowLeft = Offset(-0.2f, 0.15f), handLeft = Offset(-0.2f, 0.35f),
                hipLeft = Offset(0.05f, 0.15f), kneeLeft = Offset(0.25f, 0.15f), footLeft = Offset(0.45f, 0.15f),
                hipRight = Offset(0.05f, 0.15f), kneeRight = Offset(0.25f, 0.15f), footRight = Offset(0.45f, 0.15f)
            ),
            durationMs = 3000
        ),
        "childs_pose" to ExerciseAnimationDef(
            startPose = defaultPlank.copy(
                hipLeft = Offset(0.3f, 0.25f), hipRight = Offset(0.3f, 0.25f),
                torsoBottom = Offset(0.3f, 0.25f), torsoTop = Offset(0.0f, 0.3f),
                headOffset = Offset(-0.1f, 0.35f),
                shoulderLeft = Offset(0.0f, 0.3f), shoulderRight = Offset(0.0f, 0.3f),
                elbowLeft = Offset(-0.2f, 0.35f), elbowRight = Offset(-0.2f, 0.35f),
                handLeft = Offset(-0.4f, 0.35f), handRight = Offset(-0.4f, 0.35f)
            ),
            endPose = defaultPlank.copy(
                hipLeft = Offset(0.3f, 0.25f), hipRight = Offset(0.3f, 0.25f),
                torsoBottom = Offset(0.3f, 0.25f), torsoTop = Offset(0.0f, 0.28f),
                headOffset = Offset(-0.1f, 0.33f),
                shoulderLeft = Offset(0.0f, 0.28f), shoulderRight = Offset(0.0f, 0.28f),
                elbowLeft = Offset(-0.2f, 0.35f), elbowRight = Offset(-0.2f, 0.35f),
                handLeft = Offset(-0.4f, 0.35f), handRight = Offset(-0.4f, 0.35f)
            ),
            durationMs = 3000
        ),
        "lying_twist_stretch_left" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                kneeLeft = Offset(0.15f, -0.15f), kneeRight = Offset(0.15f, -0.15f),
                footLeft = Offset(0.25f, 0.1f), footRight = Offset(0.25f, 0.1f)
            ),
            endPose = defaultLying.copy(
                kneeLeft = Offset(0.15f, 0.1f), kneeRight = Offset(0.15f, 0.1f),
                footLeft = Offset(0.25f, 0.2f), footRight = Offset(0.25f, 0.2f)
            ),
            durationMs = 3000
        ),
        "lying_twist_stretch_right" to ExerciseAnimationDef(
            startPose = defaultLying.copy(
                kneeLeft = Offset(0.15f, -0.15f), kneeRight = Offset(0.15f, -0.15f),
                footLeft = Offset(0.25f, 0.1f), footRight = Offset(0.25f, 0.1f)
            ),
            endPose = defaultLying.copy(
                kneeLeft = Offset(0.15f, -0.3f), kneeRight = Offset(0.15f, -0.3f),
                footLeft = Offset(0.25f, -0.1f), footRight = Offset(0.25f, -0.1f)
            ),
            durationMs = 3000
        )
    )

    fun getAnimation(animationId: String): ExerciseAnimationDef {
        return exercises[animationId] ?: ExerciseAnimationDef(startPose = defaultStanding, endPose = defaultStanding)
    }
}
