package com.absforge.billing

import android.app.Activity
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class DebugBillingProvider : BillingManager {
    override fun purchaseMonthly(activity: Activity) {
        Log.d("BillingManager", "Debug: purchaseMonthly")
    }

    override fun purchaseYearly(activity: Activity) {
        Log.d("BillingManager", "Debug: purchaseYearly")
    }

    override fun purchaseLifetime(activity: Activity) {
        Log.d("BillingManager", "Debug: purchaseLifetime")
    }

    override fun restorePurchases() {
        Log.d("BillingManager", "Debug: restorePurchases")
    }

    override fun isPremium(): Flow<Boolean> = flowOf(false)
}
