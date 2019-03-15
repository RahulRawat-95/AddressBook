package com.example.address.network

import com.example.address.model.Address
import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.*

interface ApiInterface {
    @GET("/api/ams/user/addresses/")
    fun getAddresses(@Query("token") token: String = "52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78"): Observable<MutableList<Address>>

    @POST("/api/ams/user/addresses/")
    fun createAddress(
        @Query("address[firstname]") firstName: String
        , @Query("address[address1]") address1: String
        , @Query("address[address2]") address2: String
        , @Query("address[city]") city: String
        , @Query("address[state_name]") stateName: String = "Delhi"
        , @Query("address[zipcode]") zipCode: String = 110001.toString()
        , @Query("address[country_id]") countryId: Int = 105
        , @Query("address[state_id]") stateId: Int = 1400
        , @Query("address[phone]") phone: String = 9875648521.toString()
        , @Query("token") token: String = "52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78"
    ): Observable<Address>

    @PUT("/api/ams/user/addresses/{address_id}")
    fun updateAddress(
        @Path("address_id") addressId: String
        , @Query("address[firstname]") firstName: String
        , @Query("address[address1]") address1: String
        , @Query("address[address2]") address2: String
        , @Query("address[city]") city: String
        , @Query("address[state_name]") stateName: String = "Delhi"
        , @Query("address[zipcode]") zipCode: String = 110001.toString()
        , @Query("address[country_id]") countryId: Int = 105
        , @Query("address[state_id]") stateId: Int = 1400
        , @Query("address[phone]") phone: String = 9875648521.toString()
        , @Query("token") token: String = "52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78"
    ): Observable<Address>

    @DELETE("/api/ams/user/addresses/{address_id}")
    fun deleteAddress(
        @Path("address_id") addressId: String
        , @Query("token") token: String = "52e04d83e87e509f07982e6ac851e2d2c67d1d0eabc4fe78"
    ): Observable<JsonObject>
}