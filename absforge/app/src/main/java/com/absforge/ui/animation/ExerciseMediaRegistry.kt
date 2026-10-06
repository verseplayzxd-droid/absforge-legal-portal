package com.absforge.ui.animation

object ExerciseMediaRegistry {

    private val mediaMap = mapOf(
        "crunch" to Pair("img_01_crunch.jpg", "vid_01_crunch.mp4"),
        "v_crunch" to Pair("img_02_v_crunch.jpg", "vid_02_v_crunch.mp4"),
        "toe_touch" to Pair("img_03_toe_touch.jpg", "vid_03_toe_touch.mp4"),
        "knee_to_chest_crunch" to Pair("img_04_knee_to_chest_crunch.jpg", "vid_04_knee_to_chest_crunch.mp4"),
        "long_arm_crunch" to Pair("img_05_long_arm_crunch.jpg", "vid_05_long_arm_crunch.mp4"),
        "cross_arm_crunch" to Pair("img_06_cross_arm_crunch.jpg", "vid_06_cross_arm_crunch.mp4"),
        "sit_up_twist" to Pair("img_07_sit_up_twist.jpg", "vid_07_sit_up_twist.mp4"),
        "reverse_crunch" to Pair("img_08_reverse_crunch.jpg", "vid_08_reverse_crunch.mp4"),
        "leg_raise" to Pair("img_09_leg_raise.jpg", "vid_09_leg_raise.mp4"),
        "flutter_kicks" to Pair("img_10_flutter_kicks.jpg", "vid_10_flutter_kicks.mp4"),
        "single_leg_drops" to Pair("img_11_single_leg_drops.jpg", "vid_11_single_leg_drops.mp4"),
        "leg_in_out" to Pair("img_12_leg_in_out.jpg", "vid_12_leg_in_out.mp4"),
        "scissor_kicks" to Pair("img_13_scissor_kicks.jpg", "vid_13_scissor_kicks.mp4"),
        "plank" to Pair("img_14_plank.jpg", "vid_14_plank.mp4"),
        "mountain_climbers" to Pair("img_15_mountain_climbers.jpg", "vid_15_mountain_climbers.mp4"),
        "v_up" to Pair("img_16_v_up.jpg", "vid_16_v_up.mp4"),
        "bicycle_crunch" to Pair("img_17_bicycle_crunch.jpg", "vid_17_bicycle_crunch.mp4"),
        "high_knees" to Pair("img_18_high_knees.jpg", "vid_18_high_knees.mp4"),
        "dead_bug" to Pair("img_19_dead_bug.jpg", "vid_19_dead_bug.mp4"),
        "bird_dog" to Pair("img_20_bird_dog.jpg", "vid_20_bird_dog.mp4"),
        "seated_abs_circles_cw" to Pair("img_21_seated_abs_circles_cw.jpg", "vid_21_seated_abs_circles_cw.mp4"),
        "russian_twist" to Pair("img_22_russian_twist.jpg", "vid_22_russian_twist.mp4"),
        "heel_touch" to Pair("img_23_heel_touch.jpg", "vid_23_heel_touch.mp4"),
        "side_plank_left" to Pair("img_24_side_plank_left.jpg", "vid_24_side_plank_left.mp4"),
        "side_plank_right" to Pair("img_25_side_plank_right.jpg", "vid_25_side_plank_right.mp4"),
        "bent_leg_twist" to Pair("img_26_bent_leg_twist.jpg", "vid_26_bent_leg_twist.mp4"),
        "oblique_crunch" to Pair("img_27_oblique_crunch.jpg", "vid_27_oblique_crunch.mp4"),
        "standing_bicycle_crunch" to Pair("img_28_standing_bicycle_crunch.jpg", "vid_28_standing_bicycle_crunch.mp4"),
        "seated_abs_circles_ccw" to Pair("img_29_seated_abs_circles_ccw.jpg", "vid_29_seated_abs_circles_ccw.mp4"),
        "cobra_stretch" to Pair("img_30_cobra_stretch.jpg", "vid_30_cobra_stretch.mp4"),
        "childs_pose" to Pair("img_31_childs_pose.jpg", "vid_31_childs_pose.mp4"),
        "lying_twist_stretch_left" to Pair("img_32_lying_twist_stretch_left.jpg", "vid_32_lying_twist_stretch_left.mp4"),
        "lying_twist_stretch_right" to Pair("img_33_lying_twist_stretch_right.jpg", "vid_33_lying_twist_stretch_right.mp4")
    )

    fun getImageAssetPath(animationId: String): String? {
        val entry = mediaMap[animationId.lowercase()] ?: return null
        return "exercises/images/${entry.first}"
    }

    fun getVideoAssetUri(animationId: String): String? {
        val entry = mediaMap[animationId.lowercase()] ?: return null
        return "asset:///exercises/videos/${entry.second}"
    }

    fun hasVideo(animationId: String): Boolean {
        return mediaMap.containsKey(animationId.lowercase())
    }
}
