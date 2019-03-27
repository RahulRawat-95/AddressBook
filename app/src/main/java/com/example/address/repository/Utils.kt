package com.example.address.repository

import android.content.Context
import android.os.Build
import android.view.Gravity
import android.widget.Toast
import com.example.address.R
import com.example.address.model.Address

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

/**
 * method that sets values in map for sending as body parameter to api
 *
 * @param address the address value that will set the values in hashmap
 *
 * @return Unit
 */
fun MutableMap<String, String>.inflateMapWithValues(address: Address) {
    this["address[firstname]"] = address.firstName
    this["address[address1]"] = address.address1
    if (address.address2?.isBlank() == false)
        this["address[address2]"] = address.address2!!
    this["address[city]"] = address.city
    if (address.stateName?.isBlank() == false)
        this["address[state_name]"] = address.stateName!!
    this["address[zipcode]"] = address.zipCode
    this["address[country_id]"] = "105"
    this["address[state_id]"] = "1400"
    this["address[phone]"] = "9875648521"
    this["token"] = "52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78"
}

/**
 * method that tells if the device is pre lollipop or not
 */
fun isPreLollipop() = Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP