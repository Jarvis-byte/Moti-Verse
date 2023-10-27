package com.arka.quotify.HttpHandler

import android.content.Context
import android.net.ConnectivityManager

class NoInternet {
    fun isConnected(Activity: Context): Boolean {
        val connectivityManager =
            Activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        return wifiConn != null && wifiConn.isConnected || mobileConn != null && mobileConn.isConnected
    }
}