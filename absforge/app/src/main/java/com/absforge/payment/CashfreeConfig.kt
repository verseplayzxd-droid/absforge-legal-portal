package com.absforge.payment

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
    // Configured plans as requested:
    // 1 Week: $7
    // 1 Month: $20
    // Permanent: $30
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

    // Cashfree PG Credentials (user can populate or link with backend)
    var appId: String = ""
    var secretKey: String = ""
    var isProduction: Boolean = false
    var backendOrderUrl: String = ""

    const val RETURN_URL_SCHEME = "absforge://payment_success"
    const val CANCEL_URL_SCHEME = "absforge://payment_failed"
}
