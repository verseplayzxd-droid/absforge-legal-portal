package com.absforge.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

class GoogleMobileAdsConsentManager private constructor(context: Context) {

    private val consentInformation: ConsentInformation = UserMessagingPlatform.getConsentInformation(context)

    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    val isPrivacyOptionsRequired: Boolean
        get() = consentInformation.privacyOptionsRequirementStatus == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    fun gatherConsent(
        activity: Activity,
        onConsentGatheringComplete: (error: String?) -> Unit
    ) {
        val debugSettings = ConsentDebugSettings.Builder(activity)
            .build()

        val params = ConsentRequestParameters.Builder()
            .setConsentDebugSettings(debugSettings)
            .build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { formError ->
                    if (formError != null) {
                        Log.w(TAG, "Consent form error: ${formError.errorCode}: ${formError.message}")
                        onConsentGatheringComplete(formError.message)
                    } else {
                        onConsentGatheringComplete(null)
                    }
                }
            },
            { requestConsentError ->
                Log.w(TAG, "Consent update error: ${requestConsentError.errorCode}: ${requestConsentError.message}")
                onConsentGatheringComplete(requestConsentError.message)
            }
        )
    }

    fun showPrivacyOptionsForm(
        activity: Activity,
        onConsentFormDismissedListener: (error: String?) -> Unit
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { formError ->
            if (formError != null) {
                Log.w(TAG, "Privacy options form error: ${formError.errorCode}: ${formError.message}")
            }
            onConsentFormDismissedListener(formError?.message)
        }
    }

    companion object {
        private const val TAG = "ConsentManager"

        @Volatile
        private var instance: GoogleMobileAdsConsentManager? = null

        fun getInstance(context: Context): GoogleMobileAdsConsentManager =
            instance ?: synchronized(this) {
                instance ?: GoogleMobileAdsConsentManager(context.applicationContext).also { instance = it }
            }
    }
}
