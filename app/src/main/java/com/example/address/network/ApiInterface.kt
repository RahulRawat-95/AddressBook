package com.example.address.network

import com.example.address.model.Address
import com.google.gson.JsonObject
import io.reactivex.Single
import retrofit2.http.*

interface ApiInterface {
    //api to get all addresses
    @GET("/api/ams/user/addresses/")
    fun getAddresses(@Query("token") token: String = "52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78"): Single<MutableList<Address>>

    //api to create a new address
    @FormUrlEncoded
    @POST("/api/ams/user/addresses/")
    fun createAddress(@FieldMap map: Map<String, String>): Single<Address>

    //api that updates an existing address
    @FormUrlEncoded
    @PUT("/api/ams/user/addresses/{address_id}")
    fun updateAddress(
        @Path("address_id") addressId: String
        , @FieldMap map: Map<String, String>
    ): Single<Address>

    //api that deletes an address
    @DELETE("/api/ams/user/addresses/{address_id}")
    fun deleteAddress(
        @Path("address_id") addressId: String
        , @Query("token") token: String = "52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78"
    ): Single<JsonObject>
}