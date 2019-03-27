package com.example.address.connectivityListener

import android.content.Context

interface InternetListener {
    /**
     * Method that enables the Internet Listener
     *
     * @return Unit
     */
    fun enable(context: Context)

    /**
     * Method that disables the Internet Listener
     *
     * @return Unit
     */
    fun disable(context: Context)
}