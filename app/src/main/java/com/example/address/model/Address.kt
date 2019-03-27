package com.example.address.model

import android.text.TextUtils
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.example.address.BR
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlin.reflect.KMutableProperty

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
data class Address(@SerializedName("id") var id: Int) : BaseObservable(), Serializable {

    @Bindable
    @SerializedName("firstname")
    var firstName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.firstName)

            setErrors(this@Address::firstnameError, value, "Enter your Full Name")
        }

    @SerializedName("lastname")
    var lastName: String? = null

    @Bindable
    @SerializedName("address1")
    var address1: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.address1)

            setErrors(this@Address::address1Error, value, "Enter your Address Line 1")
        }

    @Bindable
    @SerializedName("address2")
    var address2: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.address2)

            setErrors(this@Address::address2Error, value, "Enter your Address Line 2")
        }

    @Bindable
    @SerializedName("city")
    var city: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.city)

            setErrors(this@Address::cityError, value, "Enter your City")
        }

    @Bindable
    @SerializedName("zipcode")
    var zipCode: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.zipCode)

            setErrors(this@Address::zipcodeError, value, "Enter your Zip Code")
        }

    @SerializedName("phone")
    var phone: String? = null

    @SerializedName("state_name")
    var stateName: String? = ""

    @SerializedName("alternative_phone")
    var alternativePhone: String? = null

    @SerializedName("company")
    var company: String? = null

    @SerializedName("state_id")
    var stateId: Int = -1

    @SerializedName("country_id")
    var countryId: Int = -1

    /**
     * method that tells if the address has all the necessary fields for creation
     *
     * @return Boolean
     */
    fun isFormValid() =
        setErrors(this@Address::firstnameError, firstName, "Enter your Full Name")
            .and(setErrors(this@Address::address1Error, address1, "Enter your Address Line 1"))
            .and(setErrors(this@Address::address2Error, address2, "Enter your Address Line 2"))
            .and(setErrors(this@Address::cityError, city, "Enter your City"))
            .and(setErrors(this@Address::zipcodeError, zipCode, "Enter your Zip Code"))

    @Bindable
    var firstnameError: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.firstnameError)
        }

    @Bindable
    var address1Error: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.address1Error)
        }

    @Bindable
    var address2Error: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.address2Error)
        }

    @Bindable
    var cityError: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.cityError)
        }

    @Bindable
    var zipcodeError: String? = null
        set(value) {
            field = value
            notifyPropertyChanged(BR.zipcodeError)
        }

    companion object {
        fun setErrors(prop: KMutableProperty<String?>, value: String?, error: String): Boolean {
            return if (TextUtils.isEmpty(value?.trim())) {
                prop.setter.call(error)
                false
            } else {
                prop.setter.call(null)
                true
            }
        }
    }
}