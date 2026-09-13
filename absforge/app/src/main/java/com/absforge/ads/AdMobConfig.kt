package com.absforge.ads

import com.absforge.BuildConfig

object AdMobConfig {

    val bannerId: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/9214589741"
        } else {
            "ca-app-pub-1624772168623970/2107620671"
        }

    val interstitialId: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/1033173712"
        } else {
            "ca-app-pub-1624772168623970/9794539006"
        }

    val rewardedId: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/5224354917"
        } else {
            "ca-app-pub-1624772168623970/4067956644"
        }

    val rewardedInterstitialId: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/5354046379"
        } else {
            "ca-app-pub-1624772168623970/6870836077"
        }

    val nativeId: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/2247696110"
        } else {
            "ca-app-pub-1624772168623970/8481457338"
        }

    val appOpenId: String
        get() = if (BuildConfig.DEBUG) {
            "ca-app-pub-3940256099942544/9257395921"
        } else {
            "ca-app-pub-1624772168623970/4707707165"
        }
}
