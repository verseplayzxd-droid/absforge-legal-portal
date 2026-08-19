package com.absforge.ads

import android.util.Log

class DebugAdsProvider : AdsManager {
    override fun showBanner() {
        Log.d("AdsManager", "Debug: showBanner called")
    }

    override fun hideBanner() {
        Log.d("AdsManager", "Debug: hideBanner called")
    }

    override fun showInterstitial(onDismissed: () -> Unit) {
        Log.d("AdsManager", "Debug: showInterstitial called")
        onDismissed()
    }

    override fun showRewarded(onRewarded: () -> Unit) {
        Log.d("AdsManager", "Debug: showRewarded called")
        onRewarded()
    }

    override fun isEnabled(): Boolean = false
}
