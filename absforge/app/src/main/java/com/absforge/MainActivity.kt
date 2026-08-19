package com.absforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.absforge.ui.navigation.AbsForgeApp
import com.absforge.ui.theme.AbsForgeBackground
import com.absforge.ui.theme.AbsForgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        com.absforge.ads.GoogleMobileAdsConsentManager.getInstance(this).gatherConsent(this) { _ ->
            com.absforge.ads.AdMobManager.init(this)
        }

        setContent {
            AbsForgeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AbsForgeBackground
                ) {
                    AbsForgeApp()
                }
            }
        }
    }
}
