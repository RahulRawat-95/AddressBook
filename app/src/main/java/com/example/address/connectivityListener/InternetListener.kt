package com.example.address.connectivityListener

import android.content.Context

interface InternetListener {
    fun enable(context: Context)

    fun disable(context: Context)
}