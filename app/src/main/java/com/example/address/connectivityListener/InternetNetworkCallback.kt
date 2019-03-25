package com.example.address.connectivityListener

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.example.address.repository.isInternetConnected

@TargetApi(21)
class InternetNetworkCallback : ConnectivityManager.NetworkCallback(), InternetListener {

    private var networkRequest: NetworkRequest =
        NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build()

    private lateinit var connectivityManager: ConnectivityManager

    private fun getNetworkManager(context: Context): ConnectivityManager {
        if (!::connectivityManager.isInitialized) {
            connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        }
        return connectivityManager
    }

    override fun enable(context: Context) {
        getNetworkManager(context).registerNetworkCallback(networkRequest, this)
    }

    override fun disable(context: Context) {
        getNetworkManager(context).unregisterNetworkCallback(this)
    }

    override fun onAvailable(network: Network) {
        isInternetConnected = true
    }

    override fun onLost(network: Network?) {
        isInternetConnected = false
    }

    override fun onUnavailable() {
        isInternetConnected = false
    }
}