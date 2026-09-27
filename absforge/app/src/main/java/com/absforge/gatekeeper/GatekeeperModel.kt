package com.absforge.gatekeeper

/**
 * Gatekeeper data model representing the remote configuration returned by
 * Master Admin Panel / Vercel Serverless API.
 */
data class GatekeeperConfig(
    val packageName: String = "",
    val minVersionCode: Int = 0,
    val latestVersionCode: Int = 0,
    val latestVersionName: String = "",
    val forceUpdate: Boolean = false,
    val flexibleUpdate: Boolean = false,
    val isMaintenance: Boolean = false,
    val maintenanceTitle: String = "Under Maintenance 🛠️",
    val maintenanceMessage: String = "We are upgrading our servers. Please check back shortly.",
    val updateTitle: String = "New Update Available! 🚀",
    val updateMessage: String = "Please update to the latest version on Google Play for new features and bug fixes.",
    val playStoreUrl: String = "https://play.google.com/store/apps/details?id=com.absforge"
)

/**
 * UI State of the Gatekeeper system.
 */
enum class GatekeeperState {
    IDLE,
    CHECKING,
    FORCE_UPDATE,
    FLEXIBLE_UPDATE,
    MAINTENANCE
}
