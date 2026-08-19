package com.absforge.ads

import com.absforge.BuildConfig

object AdMobConfig {

    val bannerId: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/9214589741"
        } else {
            "ca-app-pub-7666445916638535/5598074475"
        }

    val interstitialId: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/1033173712"
        } else {
            "ca-app-pub-7666445916638535/7716825567"
        }

    val rewardedId: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/5224354917"
        } else {
            "ca-app-pub-7666445916638535/3207448921"
        }

    val rewardedInterstitialId: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/5354046379"
        } else {
            "ca-app-pub-7666445916638535/1658829460"
        }

    val nativeId: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/2247696110"
        } else {
            "ca-app-pub-7666445916638535/6403743894"
        }

    val appOpenId: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/9257395921"
        } else {
            "ca-app-pub-7666445916638535/6719584453"
        }
}
