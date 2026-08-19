package com.absforge.billing

import android.app.Activity
import kotlinx.coroutines.flow.Flow

interface BillingManager {
    fun purchaseMonthly(activity: Activity)
    fun purchaseYearly(activity: Activity)
    fun purchaseLifetime(activity: Activity)
    fun restorePurchases()
    fun isPremium(): Flow<Boolean>
}
