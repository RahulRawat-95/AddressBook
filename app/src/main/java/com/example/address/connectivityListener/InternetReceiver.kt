package com.example.address.connectivityListener

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.example.address.repository.isInternetConnected

class InternetReceiver : BroadcastReceiver(), InternetListener {

    override fun enable(context: Context) {
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        context.registerReceiver(this, intentFilter)
    }

    override fun disable(context: Context) {
        context.unregisterReceiver(this)
    }

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