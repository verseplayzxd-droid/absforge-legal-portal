package com.absforge.ads

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.absforge.BuildConfig
import com.absforge.ui.theme.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import java.util.concurrent.atomic.AtomicBoolean

object AdMobManager {

    private const val TAG = "AdMobManager"
    private var isInitialized = AtomicBoolean(false)
    private var isPremiumUser = false

    // Global Safety State: prevents overlapping fullscreen ads
    var isFullScreenAdShowing = false
        private set

    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
    private var appOpenAd: AppOpenAd? = null
    private var appOpenLoadTime: Long = 0

    fun init(context: Context) {
        if (isInitialized.getAndSet(true)) return

        val consentManager = GoogleMobileAdsConsentManager.getInstance(context)
        if (consentManager.canRequestAds) {
            initializeMobileAds(context)
        } else {
            Log.d(TAG, "AdMob init delayed pending UMP consent")
        }
    }

    fun initializeMobileAds(context: Context) {
        try {
            MobileAds.initialize(context) { status ->
                val debugStatus = if (BuildConfig.DEBUG) "DEBUG (Google Demo Unit IDs)" else "RELEASE (Production Unit IDs)"
                Log.d(TAG, "ADMOB SDK INITIALIZED. Status=$debugStatus")
                preloadAllAds(context)
            }
        } catch (e: Exception) {
            Log.e(TAG, "AdMob SDK init error: ${e.message}", e)
        }
    }

    fun setPremiumUser(isPremium: Boolean) {
        isPremiumUser = isPremium
    }

    fun isPremium(): Boolean = isPremiumUser

    private fun preloadAllAds(context: Context) {
        if (isPremiumUser) return
        loadInterstitial(context)
        loadRewarded(context)
        loadRewardedInterstitial(context)
        loadAppOpenAd(context)
    }

    // --- INTERSTITIAL ADS ---
    fun loadInterstitial(context: Context) {
        if (isPremiumUser || interstitialAd != null) return
        val req = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            AdMobConfig.interstitialId,
            req,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "ADMOB INTERSTITIAL LOADED (${ad.adUnitId})")
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d(TAG, "ADMOB INTERSTITIAL FAILED TO LOAD: ${error.message}")
                    interstitialAd = null
                }
            }
        )
    }

    fun showMidWorkoutInterstitial(activity: Activity, setIndex: Int, onClosed: () -> Unit) {
        if (isPremiumUser || isFullScreenAdShowing) {
            onClosed()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            isFullScreenAdShowing = true
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "ADMOB SET INTERSTITIAL CLOSED setIndex=$setIndex")
                    isFullScreenAdShowing = false
                    interstitialAd = null
                    loadInterstitial(activity)
                    onClosed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    Log.d(TAG, "ADMOB SET INTERSTITIAL FAILED TO SHOW: ${error.message}")
                    isFullScreenAdShowing = false
                    interstitialAd = null
                    onClosed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "ADMOB SET INTERSTITIAL SHOWING setIndex=$setIndex")
                }
            }
            ad.show(activity)
        } else {
            loadInterstitial(activity)
            onClosed()
        }
    }

    fun showInterstitial(
        activity: Activity,
        tag: String = "workout_complete_interstitial",
        onClosed: () -> Unit
    ) {
        if (isPremiumUser || isFullScreenAdShowing) {
            onClosed()
            return
        }

        val ad = interstitialAd
        if (ad != null) {
            isFullScreenAdShowing = true
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "ADMOB FINAL INTERSTITIAL CLOSED tag=$tag")
                    isFullScreenAdShowing = false
                    interstitialAd = null
                    loadInterstitial(activity)
                    onClosed()
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    Log.d(TAG, "ADMOB FINAL INTERSTITIAL FAILED TO SHOW: ${error.message}")
                    isFullScreenAdShowing = false
                    interstitialAd = null
                    onClosed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "ADMOB FINAL INTERSTITIAL SHOWING tag=$tag")
                }
            }
            ad.show(activity)
        } else {
            loadInterstitial(activity)
            onClosed()
        }
    }

    // --- REWARDED ADS ---
    fun loadRewarded(context: Context) {
        if (isPremiumUser || rewardedAd != null) return
        val req = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            AdMobConfig.rewardedId,
            req,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "ADMOB REWARDED LOADED")
                    rewardedAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d(TAG, "ADMOB REWARDED FAILED TO LOAD: ${error.message}")
                    rewardedAd = null
                }
            }
        )
    }

    fun showRewarded(
        activity: Activity,
        tag: String = "bonus_workout_rewarded",
        onRewarded: (Boolean) -> Unit
    ) {
        if (isFullScreenAdShowing) {
            onRewarded(false)
            return
        }

        val ad = rewardedAd
        if (ad != null) {
            isFullScreenAdShowing = true
            var rewardEarned = false

            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "ADMOB REWARDED CLOSED earned=$rewardEarned")
                    isFullScreenAdShowing = false
                    rewardedAd = null
                    loadRewarded(activity)
                    onRewarded(rewardEarned)
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    Log.d(TAG, "ADMOB REWARDED FAILED TO SHOW: ${error.message}")
                    isFullScreenAdShowing = false
                    rewardedAd = null
                    onRewarded(false)
                }
            }

            ad.show(activity) { rewardItem ->
                Log.d(TAG, "ADMOB REWARD EARNED: ${rewardItem.amount} ${rewardItem.type}")
                rewardEarned = true
            }
        } else {
            loadRewarded(activity)
            onRewarded(false)
        }
    }

    // --- REWARDED INTERSTITIAL ADS ---
    fun loadRewardedInterstitial(context: Context) {
        if (isPremiumUser || rewardedInterstitialAd != null) return
        val req = AdRequest.Builder().build()
        RewardedInterstitialAd.load(
            context,
            AdMobConfig.rewardedInterstitialId,
            req,
            object : RewardedInterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedInterstitialAd) {
                    Log.d(TAG, "ADMOB REWARDED INTERSTITIAL LOADED")
                    rewardedInterstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d(TAG, "ADMOB REWARDED INTERSTITIAL FAILED: ${error.message}")
                    rewardedInterstitialAd = null
                }
            }
        )
    }

    fun showRewardedInterstitial(
        activity: Activity,
        tag: String = "bonus_finisher_rewarded_interstitial",
        onRewarded: (Boolean) -> Unit
    ) {
        if (isFullScreenAdShowing) {
            onRewarded(false)
            return
        }

        val ad = rewardedInterstitialAd
        if (ad != null) {
            isFullScreenAdShowing = true
            var rewardEarned = false

            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "ADMOB REWARDED INTERSTITIAL CLOSED earned=$rewardEarned")
                    isFullScreenAdShowing = false
                    rewardedInterstitialAd = null
                    loadRewardedInterstitial(activity)
                    onRewarded(rewardEarned)
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    Log.d(TAG, "ADMOB REWARDED INTERSTITIAL FAILED TO SHOW: ${error.message}")
                    isFullScreenAdShowing = false
                    rewardedInterstitialAd = null
                    onRewarded(false)
                }
            }

            ad.show(activity) { rewardItem ->
                Log.d(TAG, "ADMOB REWARDED INTERSTITIAL EARNED: ${rewardItem.amount}")
                rewardEarned = true
            }
        } else {
            loadRewardedInterstitial(activity)
            onRewarded(false)
        }
    }

    // --- APP OPEN ADS ---
    fun loadAppOpenAd(context: Context) {
        if (isPremiumUser || appOpenAd != null) return
        val req = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            AdMobConfig.appOpenId,
            req,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    Log.d(TAG, "ADMOB APP OPEN LOADED")
                    appOpenAd = ad
                    appOpenLoadTime = System.currentTimeMillis()
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d(TAG, "ADMOB APP OPEN FAILED TO LOAD: ${error.message}")
                    appOpenAd = null
                }
            }
        )
    }

    fun showAppOpenAdIfAvailable(activity: Activity) {
        if (isPremiumUser || isFullScreenAdShowing) return
        val ad = appOpenAd
        val isExpired = (System.currentTimeMillis() - appOpenLoadTime) > (4 * 3600 * 1000L)

        if (ad != null && !isExpired) {
            isFullScreenAdShowing = true
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "ADMOB APP OPEN DISMISSED")
                    isFullScreenAdShowing = false
                    appOpenAd = null
                    loadAppOpenAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(error: AdError) {
                    Log.d(TAG, "ADMOB APP OPEN FAILED TO SHOW: ${error.message}")
                    isFullScreenAdShowing = false
                    appOpenAd = null
                    loadAppOpenAd(activity)
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "ADMOB APP OPEN SHOWED")
                }
            }
            ad.show(activity)
        } else {
            loadAppOpenAd(activity)
        }
    }
}

// --- ADMOB AD BANNERS ---
@Composable
fun AbsForgeAdBanner(
    modifier: Modifier = Modifier,
    tag: String = "home_bottom_banner"
) {
    if (AdMobManager.isPremium()) return

    var isFailed by remember { mutableStateOf(false) }

    if (isFailed) {
        Box(modifier = modifier.height(0.dp))
        return
    }

    val context = LocalContext.current
    val isInPreview = LocalInspectionMode.current

    if (isInPreview) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(AbsForgeSurfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Text("AdMob Banner Preview", color = AbsForgeTextSecondary, fontSize = 12.sp)
        }
        return
    }

    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = AdMobConfig.bannerId
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d("AdMobManager", "ADMOB BANNER LOADED tag=$tag")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.d("AdMobManager", "ADMOB BANNER FAILED tag=$tag error=${error.message}")
                        isFailed = true
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        }
    )
}

// --- ADMOB NATIVE ADVANCED CARD ---
@Composable
fun AdMobNativeCard(
    modifier: Modifier = Modifier,
    tag: String = "feed_native_ad"
) {
    if (AdMobManager.isPremium()) return

    var nativeAdState by remember { mutableStateOf<NativeAd?>(null) }
    var isFailed by remember { mutableStateOf(false) }
    val context = LocalContext.current

    DisposableEffect(context) {
        val adLoader = AdLoader.Builder(context, AdMobConfig.nativeId)
            .forNativeAd { ad ->
                nativeAdState = ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.d("AdMobManager", "ADMOB NATIVE FAILED tag=$tag error=${error.message}")
                    isFailed = true
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().build())
            .build()

        adLoader.loadAd(AdRequest.Builder().build())

        onDispose {
            nativeAdState?.destroy()
        }
    }

    if (isFailed || nativeAdState == null) {
        Box(modifier = modifier.height(0.dp))
        return
    }

    val ad = nativeAdState ?: return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = AbsForgeSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AbsForgeBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    color = AbsForgePrimary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "SPONSORED",
                        color = AbsForgePrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                ad.advertiser?.let {
                    Text(text = it, color = AbsForgeTextSecondary, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = ad.headline ?: "",
                color = AbsForgeTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            ad.body?.let { bodyText ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bodyText,
                    color = AbsForgeTextSecondary,
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }

            ad.callToAction?.let { cta ->
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = AbsForgePrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = cta, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
