package com.absforge.payment

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

sealed class OrderCreationResult {
    data class Success(val orderId: String, val paymentSessionId: String) : OrderCreationResult()
    data class Error(val message: String) : OrderCreationResult()
}

object CashfreeClient {
    private const val TAG = "CashfreeClient"

    suspend fun createOrder(
        plan: PremiumPlan,
        customerPhone: String = "9876543210",
        customerEmail: String = "support@absforge.app",
        customerName: String = "AbsForge User"
    ): OrderCreationResult = withContext(Dispatchers.IO) {
        try {
            val orderId = "ORD_AF_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(4).uppercase()}"
            val endpoint = "${CashfreeConfig.baseUrl}/orders"
            val url = URL(endpoint)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                doInput = true
                connectTimeout = 15000
                readTimeout = 15000
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("x-client-id", CashfreeConfig.appId)
                setRequestProperty("x-client-secret", CashfreeConfig.secretKey)
                setRequestProperty("x-api-version", CashfreeConfig.apiVersion)
            }

            val customerObj = JSONObject().apply {
                put("customer_id", "cust_${UUID.randomUUID().toString().take(8)}")
                put("customer_phone", customerPhone)
                put("customer_email", customerEmail)
                put("customer_name", customerName)
            }

            val orderMeta = JSONObject().apply {
                put("return_url", CashfreeConfig.returnUrl)
            }

            val body = JSONObject().apply {
                put("order_id", orderId)
                put("order_amount", plan.amountInr)
                put("order_currency", "INR")
                put("customer_details", customerObj)
                put("order_meta", orderMeta)
            }

            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(body.toString())
                writer.flush()
            }

            val responseCode = conn.responseCode
            val responseStream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val responseText = BufferedReader(InputStreamReader(responseStream)).use { it.readText() }

            Log.d(TAG, "Cashfree createOrder status: $responseCode, response: $responseText")

            if (responseCode in 200..299) {
                val json = JSONObject(responseText)
                val paymentSessionId = json.optString("payment_session_id")
                if (paymentSessionId.isNotBlank()) {
                    OrderCreationResult.Success(orderId, paymentSessionId)
                } else {
                    OrderCreationResult.Error("No payment session id returned: $responseText")
                }
            } else {
                val json = try { JSONObject(responseText) } catch (e: Exception) { null }
                val errorMsg = json?.optString("message") ?: "Error $responseCode from Cashfree"
                OrderCreationResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Cashfree createOrder failed", e)
            OrderCreationResult.Error("Connection error: ${e.message}")
        }
    }

    suspend fun getOrderStatus(orderId: String): String = withContext(Dispatchers.IO) {
        try {
            val endpoint = "${CashfreeConfig.baseUrl}/orders/$orderId"
            val url = URL(endpoint)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 10000
                readTimeout = 10000
                setRequestProperty("x-client-id", CashfreeConfig.appId)
                setRequestProperty("x-client-secret", CashfreeConfig.secretKey)
                setRequestProperty("x-api-version", CashfreeConfig.apiVersion)
            }

            val responseCode = conn.responseCode
            val responseStream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val responseText = BufferedReader(InputStreamReader(responseStream)).use { it.readText() }

            if (responseCode in 200..299) {
                val json = JSONObject(responseText)
                json.optString("order_status", "UNKNOWN")
            } else {
                "ERROR_$responseCode"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Cashfree getOrderStatus failed", e)
            "ERROR"
        }
    }
}
