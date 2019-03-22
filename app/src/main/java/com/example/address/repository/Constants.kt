package com.example.address.repository

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import com.example.address.R

const val DEFAULT_ADDRESS_ID = "default_address_id"

/**
 * @property defaultAddressId address id of the default address
 */
var defaultAddressId: Int = -1

/**
 * @property isInternetConnected to tell whether Internet is connected or not
 */
var isInternetConnected: Boolean? = null

/**
 * @property toast to show and cancel Toasts
 */
var toast: Toast? = null

/**
 * method that shows error toast of Please Try Again
 *
 * @param context the context object to show Toast
 *
 * @return Unit
 */
fun showErrorToast(context: Context, e: Throwable) {
    if (toast != null) {
        toast?.cancel()
    }
    toast = when {
        isInternetConnected != null && !(isInternetConnected!!) -> {
            Toast.makeText(context, R.string.error_internet_connect, Toast.LENGTH_LONG)
                .apply { setGravity(Gravity.CENTER, 0, 0) }
        }
        else -> Toast.makeText(context, R.string.error_load_data, Toast.LENGTH_LONG)
            .apply { setGravity(Gravity.CENTER, 0, 0) }
    }
    toast?.show()
}