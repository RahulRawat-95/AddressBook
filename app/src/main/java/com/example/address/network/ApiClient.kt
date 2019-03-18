package com.example.address.network

import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @property BASE_URL the base url of hosting api
 *
 * @property apiInterface the Api Interface class that lists all the api endpoints and their respective responses
 */
object ApiClient {
    const val BASE_URL = "https://shop-spree.herokuapp.com/"

    val apiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}