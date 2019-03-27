package com.example.address.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.address.R
import com.example.address.repository.defaultAddressId
import com.example.address.repository.getDefaultAddressId

class SplashActivity : AppCompatActivity() {

    private val handler by lazy { Handler() }

    private val runnable by lazy {
        Runnable {
            defaultAddressId = getDefaultAddressId()

            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
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