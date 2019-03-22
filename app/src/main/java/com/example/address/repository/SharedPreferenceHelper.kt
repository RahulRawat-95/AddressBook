package com.example.address.repository

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

private fun Context.getSharedPreferences(): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(this)
}

fun Context.getInt(key: String): Int {
    return getSharedPreferences().getInt(key, -1)
}

fun Context.putInt(key: String, value: Int) {
    getSharedPreferences()
        .edit()
        .putInt(key, value)
        .commit()
}

fun Context.getDefaultAddressId(): Int {
    return getInt(DEFAULT_ADDRESS_ID)
}

fun Context.putDefaultAddressId(value: Int) {
    return putInt(DEFAULT_ADDRESS_ID, value)
}