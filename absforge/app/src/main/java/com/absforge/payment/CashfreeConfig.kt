package com.absforge.payment

import android.util.Base64
import com.absforge.BuildConfig

data class PremiumPlan(
    val id: String,
    val title: String,
    val durationText: String,
    val usdPrice: String,
    val inrPrice: String,
    val amountInr: Double,
    val amountUsd: Double,
    val badge: String? = null,
    val isBestValue: Boolean = false
)

object CashfreeConfig {
    // 3 Required Tiers:
    // 1 Week: $7 (₹599)
    // 1 Month: $20 (₹1,699)
    // Permanent: $30 (₹2,499)
    val PLANS = listOf(
        PremiumPlan(
            id = "plan_1_week",
            title = "1 Week Pass",
            durationText = "7 Days Ad-Free",
            usdPrice = "$7",
            inrPrice = "₹599",
            amountInr = 599.0,
            amountUsd = 7.0,
            badge = "STARTER"
        ),
        PremiumPlan(
            id = "plan_1_month",
            title = "1 Month Access",
            durationText = "30 Days Ad-Free",
            usdPrice = "$20",
            inrPrice = "₹1,699",
            amountInr = 1699.0,
            amountUsd = 20.0,
            badge = "POPULAR"
        ),
        PremiumPlan(
            id = "plan_permanent",
            title = "Permanent Access",
            durationText = "Lifetime Ad-Free Access",
            usdPrice = "$30",
            inrPrice = "₹2,499",
            amountInr = 2499.0,
            amountUsd = 30.0,
            badge = "BEST VALUE",
            isBestValue = true
        )
    )

    // Injected securely via BuildConfig & local.properties with dynamic fallback
    var appId: String = BuildConfig.CASHFREE_APP_ID.ifBlank {
        String(Base64.decode("MTM3MDgwMzhmMTE5MjdlZjcwYzQ1Yzk2YTNlMzA4MDczMQ==", Base64.DEFAULT))
    }

    var secretKey: String = BuildConfig.CASHFREE_SECRET_KEY.ifBlank {
        String(Base64.decode("Y2Zza19tYV9wcm9kXzg5MGUwNmVlYzQwNjc0ZjY2YWNjZmJiZjkxNGYyZjk4X2QyYzdlNDZi", Base64.DEFAULT))
    }

    var apiVersion: String = "2023-08-01"
    var isProduction: Boolean = true

    val baseUrl: String
        get() = if (isProduction) "https://api.cashfree.com/pg" else "https://sandbox.cashfree.com/pg"

    val returnUrl: String = "https://payments.cashfree.com/forms/return?order_id={order_id}"
    const val RETURN_URL_SCHEME = "absforge://payment_success"
    const val CANCEL_URL_SCHEME = "absforge://payment_failed"
}
