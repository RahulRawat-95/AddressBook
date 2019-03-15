package com.example.address.model

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Address(@SerializedName("id") var id: Int) : Serializable {

    @SerializedName("firstname")
    var firstName: String = ""

    @SerializedName("lastname")
    var lastName: String? = null

    @SerializedName("address1")
    var address1: String = ""

    @SerializedName("address2")
    var address2: String = ""

    @SerializedName("city")
    var city: String = ""

    @SerializedName("zipcode")
    var zipCode: String = ""

    @SerializedName("phone")
    var phone: String? = null

    @SerializedName("state_name")
    var stateName: String = ""

    @SerializedName("alternative_phone")
    var alternativePhone: String? = null

    @SerializedName("company")
    var company: String? = null

    @SerializedName("state_id")
    var stateId: Int = -1

    @SerializedName("country_id")
    var countryId: Int = -1

    fun isFormValid() =
        !TextUtils.isEmpty(firstName)
                && !TextUtils.isEmpty(address1)
                && !TextUtils.isEmpty(address2)
                && !TextUtils.isEmpty(city)
                && !TextUtils.isEmpty(zipCode)
}