package com.absforge.ads

interface AdsManager {
    fun showBanner()
    fun hideBanner()
    fun showInterstitial(onDismissed: () -> Unit)
    fun showRewarded(onRewarded: () -> Unit)
    fun isEnabled(): Boolean
}
