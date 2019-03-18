package com.example.address.repository

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import com.example.address.R

/**
 * @property defaultAddressId address id of the default address
 */
var defaultAddressId: Int = -1

/**
 * method that shows error toast of Please Try Again
 *
 * @param context the context object to show Toast
 *
 * @return Unit
 */
fun showErrorToast(context: Context) {
    Toast.makeText(context, R.string.error_load_data, Toast.LENGTH_LONG)
        .apply { setGravity(Gravity.CENTER, 0, 0) }
        .show()
}