package com.michael.apitester.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager



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

