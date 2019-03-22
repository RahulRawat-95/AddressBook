package com.example.address.activity

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.address.R
import com.example.address.repository.defaultAddressId
import com.example.address.repository.getDefaultAddressId
import com.example.address.repository.isInternetConnected

class SplashActivity : AppCompatActivity() {

    val handler by lazy { Handler() }

    val runnable by lazy {
        object : Runnable {
            override fun run() {
                defaultAddressId = getDefaultAddressId()

                val connectivityManager =
                    this@SplashActivity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val networkInfo = connectivityManager.activeNetworkInfo

                isInternetConnected = networkInfo != null && networkInfo.isConnected

                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        //delayed start to Main Activity
        handler.postDelayed(runnable, 1000)
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }

}