package com.example.apitester.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.Executors
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


fun makeRequest(
    method: String,
    url: String,
    headers: Map<String, String>,
    parameters: Map<String, String>,
    body: String?,
    onResult: (response: String?, responseCode: Int?, error: Exception?) -> Unit
) {
    val mainHandler = Handler(Looper.getMainLooper())
    Executors.newSingleThreadExecutor().execute {
        var connection: HttpURLConnection? = null
        try {
            val urlWithParams = if (parameters.isNotEmpty()) {
                val paramString = parameters.entries.joinToString("&") {
                    "${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}"
                }
                "$url?$paramString"
            } else {
                url
            }
            // Create custom HttpURLConnection
            Log.d("makeRequest","requestUrl= $urlWithParams")
            connection = createHttpURLConnection(urlWithParams)
            connection.requestMethod = method

            // Set headers
            headers.forEach { (key, value) ->
                if(key.isNotEmpty() && value.isNotEmpty()) {
                    connection.setRequestProperty(key, value)
                }
            }

            if (method in listOf("POST", "PUT", "PATCH")) {
                connection.doOutput = true
                connection.outputStream.use { outputStream ->
                    if (!body.isNullOrEmpty()) {
                        outputStream.write(body.toByteArray())
                    } else if (parameters.isNotEmpty()) {
                        val formParams = parameters.map { "${it.key}=${it.value}" }.joinToString("&")
                        outputStream.write(formParams.toByteArray())
                    }
                }
            }

            // Get response code
            val responseCode = connection.responseCode
            val response = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d("makeRequest", "Response: $response")  // Log the response

            // Post response back to main thread
            mainHandler.post {
                onResult(response, responseCode, null)
            }
        } catch (e: Exception) {
            Log.e("makeRequest", "Error: ${e.message}", e)  // Log the error

            // Post error back to main thread
            mainHandler.post {
                onResult(null, null, e)
            }
        } finally {
            connection?.disconnect()
        }
    }
}

fun createHttpURLConnection(urlString: String): HttpURLConnection {
    val url = URL(urlString)
    val urlConnection = url.openConnection() as HttpURLConnection

    if (urlConnection is HttpsURLConnection) {
        try {
            val trustAllCerts = createAllTrustingTrustManager()
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())
            urlConnection.sslSocketFactory = sslContext.socketFactory
            urlConnection.hostnameVerifier = HostnameVerifier { _, _ -> true }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return urlConnection
}

fun createAllTrustingTrustManager(): Array<TrustManager> {
    return arrayOf(@SuppressLint("CustomX509TrustManager")
    object : X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        @SuppressLint("TrustAllX509TrustManager")
        override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })
}



fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkCapabilities = connectivityManager.activeNetwork?.let {
        connectivityManager.getNetworkCapabilities(it)
    }
    return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}

