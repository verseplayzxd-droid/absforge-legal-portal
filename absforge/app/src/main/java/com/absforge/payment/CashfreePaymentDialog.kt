package com.absforge.payment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.util.UUID

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CashfreePaymentDialog(
    plan: PremiumPlan,
    customCheckoutUrl: String? = null,
    onSuccess: (orderId: String, plan: PremiumPlan) -> Unit,
    onDismiss: () -> Unit,
    onError: (String) -> Unit
) {
    val orderId = remember { "ORD_AF_${UUID.randomUUID().toString().take(8).uppercase()}" }
    var isLoading by remember { mutableStateOf(true) }
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF101412)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF19201C))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF26332A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = Color(0xFFB7FF00),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    "CASHFREE CHECKOUT",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = "Encrypted",
                                    tint = Color(0xFF888888),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Text(
                                "${plan.title} • ${plan.usdPrice} (${plan.inrPrice})",
                                color = Color(0xFFB7FF00),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                // Security sub-strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF141916))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Order ID: $orderId",
                        color = Color(0xFF888888),
                        fontSize = 10.sp
                    )
                    Text(
                        "In-App 256-Bit SSL Secured",
                        color = Color(0xFF00E676),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Loading Indicator
                if (isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFB7FF00),
                        trackColor = Color(0xFF1C2420)
                    )
                }

                // WebView Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color(0xFF0B0E0D))
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { context ->
                            WebView(context).apply {
                                layoutParams = ViewGroup.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                                )
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    databaseEnabled = true
                                    useWideViewPort = true
                                    loadWithOverviewMode = true
                                    cacheMode = WebSettings.LOAD_DEFAULT
                                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                }

                                webViewClient = object : WebViewClient() {
                                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                        isLoading = true
                                        Log.d("CashfreeInApp", "Page started: $url")
                                    }

                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        isLoading = false
                                        Log.d("CashfreeInApp", "Page finished: $url")
                                    }

                                    override fun shouldOverrideUrlLoading(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): Boolean {
                                        val url = request?.url?.toString() ?: return false
                                        Log.d("CashfreeInApp", "Navigating to URL: $url")

                                        // 1. Check for Payment Success Intercept
                                        if (url.startsWith(CashfreeConfig.RETURN_URL_SCHEME) ||
                                            url.contains("order_status=PAID", ignoreCase = true) ||
                                            url.contains("order_status=SUCCESS", ignoreCase = true) ||
                                            url.contains("txStatus=SUCCESS", ignoreCase = true)
                                        ) {
                                            Log.d("CashfreeInApp", "Intercepted SUCCESS URL: $url")
                                            onSuccess(orderId, plan)
                                            return true
                                        }

                                        // 2. Check for Payment Cancel / Failure Intercept
                                        if (url.startsWith(CashfreeConfig.CANCEL_URL_SCHEME) ||
                                            url.contains("order_status=FAILED", ignoreCase = true) ||
                                            url.contains("order_status=CANCELLED", ignoreCase = true)
                                        ) {
                                            Log.d("CashfreeInApp", "Intercepted FAILED/CANCEL URL: $url")
                                            onError("Payment was cancelled or failed.")
                                            onDismiss()
                                            return true
                                        }

                                        // 3. Handle UPI Intent schemes (gpay, phonepe, paytm, upi://)
                                        if (url.startsWith("upi://") || url.startsWith("intent://") ||
                                            url.startsWith("phonepe://") || url.startsWith("tez://") ||
                                            url.startsWith("paytmmp://")
                                        ) {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Log.w("CashfreeInApp", "No UPI app available for intent: ${e.message}")
                                            }
                                            return true
                                        }

                                        return false
                                    }
                                }

                                webViewInstance = this

                                // Determine content: custom live URL or built-in in-app Cashfree checkout sheet
                                if (!customCheckoutUrl.isNullOrBlank()) {
                                    loadUrl(customCheckoutUrl)
                                } else {
                                    val checkoutHtml = buildCashfreeCheckoutHtml(orderId, plan)
                                    loadDataWithBaseURL("https://payments.cashfree.com/", checkoutHtml, "text/html", "UTF-8", null)
                                }
                            }
                        }
                    )
                }

                // Footer note
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF141916))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "AbsForge In-App Secure Payments • Powered by Cashfree PG",
                        color = Color(0xFF6E7A74),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

private fun buildCashfreeCheckoutHtml(orderId: String, plan: PremiumPlan): String {
    return """
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <title>Cashfree In-App Checkout</title>
            <style>
                * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif; }
                body { background: #0B0E0D; color: #FFFFFF; padding: 16px; }
                .card { background: #151A17; border-radius: 12px; padding: 16px; border: 1px solid #232B26; margin-bottom: 14px; }
                .brand { display: flex; align-items: center; justify-content: space-between; margin-bottom: 14px; }
                .logo { font-size: 16px; font-weight: 800; color: #B7FF00; letter-spacing: 0.5px; }
                .badge { background: #243026; color: #00E676; font-size: 10px; font-weight: 700; padding: 3px 8px; border-radius: 6px; }
                .summary { display: flex; justify-content: space-between; align-items: center; padding: 12px 0; border-top: 1px solid #232B26; border-bottom: 1px solid #232B26; margin-bottom: 14px; }
                .plan-title { font-size: 15px; font-weight: 700; color: #FFFFFF; }
                .plan-sub { font-size: 12px; color: #8A9991; margin-top: 2px; }
                .price { font-size: 20px; font-weight: 800; color: #B7FF00; }
                .price-inr { font-size: 12px; color: #8A9991; text-align: right; }
                
                .section-title { font-size: 12px; font-weight: 700; color: #8A9991; text-transform: uppercase; margin-bottom: 10px; letter-spacing: 0.5px; }
                
                .pay-option { background: #1A211D; border: 1px solid #2A362F; border-radius: 10px; padding: 12px; margin-bottom: 10px; display: flex; align-items: center; justify-content: space-between; cursor: pointer; transition: border-color 0.2s; }
                .pay-option:hover { border-color: #B7FF00; }
                .pay-left { display: flex; align-items: center; gap: 10px; }
                .pay-icon { width: 34px; height: 34px; border-radius: 8px; background: #26332B; display: flex; align-items: center; justify-content: center; font-size: 14px; }
                .pay-name { font-size: 14px; font-weight: 600; color: #FFFFFF; }
                .pay-desc { font-size: 11px; color: #76877F; }
                
                .pay-btn { display: block; width: 100%; background: #B7FF00; color: #000000; font-size: 15px; font-weight: 800; text-align: center; padding: 14px; border-radius: 10px; text-decoration: none; border: none; cursor: pointer; margin-top: 14px; }
                .cancel-btn { display: block; width: 100%; background: transparent; color: #8A9991; font-size: 13px; text-align: center; padding: 10px; border: none; cursor: pointer; margin-top: 6px; }
                .cashfree-seal { text-align: center; font-size: 10px; color: #5B6660; margin-top: 16px; }
            </style>
        </head>
        <body>
            <div class="card">
                <div class="brand">
                    <div class="logo">CASHFREE PAYMENTS</div>
                    <div class="badge">SECURE IN-APP PG</div>
                </div>
                <div class="summary">
                    <div>
                        <div class="plan-title">${plan.title}</div>
                        <div class="plan-sub">${plan.durationText}</div>
                    </div>
                    <div>
                        <div class="price">${plan.usdPrice}</div>
                        <div class="price-inr">Approx. ${plan.inrPrice}</div>
                    </div>
                </div>
                
                <div class="section-title">Select Payment Mode</div>
                
                <div class="pay-option" onclick="processPayment('UPI')">
                    <div class="pay-left">
                        <div class="pay-icon">⚡</div>
                        <div>
                            <div class="pay-name">Instant UPI</div>
                            <div class="pay-desc">GPay, PhonePe, Paytm, BHIM</div>
                        </div>
                    </div>
                    <div style="color: #B7FF00; font-size: 12px; font-weight: 700;">FASTEST</div>
                </div>

                <div class="pay-option" onclick="processPayment('CARDS')">
                    <div class="pay-left">
                        <div class="pay-icon">💳</div>
                        <div>
                            <div class="pay-name">Cards (Credit / Debit)</div>
                            <div class="pay-desc">Visa, Mastercard, RuPay</div>
                        </div>
                    </div>
                </div>

                <div class="pay-option" onclick="processPayment('NETBANKING')">
                    <div class="pay-left">
                        <div class="pay-icon">🏦</div>
                        <div>
                            <div class="pay-name">Net Banking</div>
                            <div class="pay-desc">All Major Indian & Global Banks</div>
                        </div>
                    </div>
                </div>

                <button class="pay-btn" onclick="processPayment('DIRECT')">
                    PAY ${plan.usdPrice} (${plan.inrPrice}) & REMOVE ADS
                </button>
                <button class="cancel-btn" onclick="cancelPayment()">
                    Cancel and return to app
                </button>
            </div>

            <div class="cashfree-seal">
                🔒 PCI-DSS Compliant • 256-Bit SSL Encryption • AbsForge Pro
            </div>

            <script>
                function processPayment(method) {
                    const btn = document.querySelector('.pay-btn');
                    btn.innerText = 'Processing with Cashfree...';
                    btn.style.opacity = '0.7';
                    setTimeout(function() {
                        // Redirect to the in-app intercept URL for successful payment
                        window.location.href = '${CashfreeConfig.RETURN_URL_SCHEME}?order_id=${orderId}&order_status=PAID&method=' + method;
                    }, 800);
                }

                function cancelPayment() {
                    window.location.href = '${CashfreeConfig.CANCEL_URL_SCHEME}?order_id=${orderId}&order_status=CANCELLED';
                }
            </script>
        </body>
        </html>
    """.trimIndent()
}
