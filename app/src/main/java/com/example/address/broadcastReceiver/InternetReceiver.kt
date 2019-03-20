package com.example.address.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.example.address.repository.isInternetConnected

class InternetReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ConnectivityManager.CONNECTIVITY_ACTION -> {
            }
            WifiManager.WIFI_STATE_CHANGED_ACTION -> {
            }
            WifiManager.NETWORK_STATE_CHANGED_ACTION -> {
            }
            else -> return
        }

        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo = connectivityManager.activeNetworkInfo

        isInternetConnected = networkInfo != null && networkInfo.isConnected
    }

}