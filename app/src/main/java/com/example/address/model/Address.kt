package com.example.address.model

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * class that represents an Address
 *
 * @property id the id of this address as determined by Server
 * @property firstName the first name of Person living at Address
 * @property lastName the last name of Person living at Address
 * @property address1 the first line of Address
 * @property address2 the second line of Address
 * @property city the city this address is from
 * @property zipCode the zip code for this address
 * @property phone the phone number at this address
 * @property stateName the name of state in which this address resides
 * @property alternativePhone the alternative phone number at this address
 * @property company the company to which this address belongs
 * @property stateId the state id of the state in which this address belong
 * @property countryId the country id of the country in which this address belong
 *
 * @constructor Creates a new Address with a default ID that was provided
 */
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

    /**
     * method that tells if the address has all the neccesary fields for creation
     *
     * @return Boolean
     */
    fun isFormValid() =
        !TextUtils.isEmpty(firstName)
                && !TextUtils.isEmpty(address1)
                && !TextUtils.isEmpty(address2)
                && !TextUtils.isEmpty(city)
                && !TextUtils.isEmpty(zipCode)
}