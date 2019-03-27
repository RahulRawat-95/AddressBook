package com.example.address.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.address.model.Address
import com.example.address.network.ApiClient
import com.example.address.repository.inflateMapWithValues
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.lang.reflect.Field

/**
 * View Model class for AddEditAddressActivity
 *
 * @property application the application object
 */
class AddEditAddressViewModel(application: Application) : AndroidViewModel(application) {

    private val jsonParser by lazy { JsonParser() }

    /**
     * Method that creates an Address and calls the api
     *
     * @param address the address to be created
     */
    fun createAddress(address: Address): Single<Address> {
        return ApiClient.apiInterface.createAddress(
            HashMap<String, String>().apply { inflateMapWithValues(address) }
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { handleError(address, it) }
    }

    /**
     * Method that updates an Address and calls the api
     *
     * @param address the address to be updated
     */
    fun updateAddress(address: Address): Single<Address> {
        return ApiClient.apiInterface.updateAddress(
            address.id.toString(),
            HashMap<String, String>().apply { inflateMapWithValues(address) }
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError { handleError(address, it) }
    }

    /**
     * Method that handles errors for creation or updation of addresses
     *
     * @param address the address parameter on which the api call was unsuccessfull
     * @param e the error that api call returned
     *
     * @return Unit
     */
    private fun handleError(address: Address, e: Throwable) {
        try {
            val httpException = e as? HttpException
            val errorBody = httpException?.response()?.errorBody()?.string()
            if (errorBody != null) {
                val errors = jsonParser.parse(errorBody).asJsonObject.getAsJsonObject("errors")
                val singleErrorMessage = StringBuilder("")
                var values: JsonArray
                var field: Field
                for ((key, value) in errors.entrySet()) {
                    singleErrorMessage.clear()
                    singleErrorMessage.append(key)
                    singleErrorMessage.append(" ")
                    if (value.isJsonArray) {
                        values = value.asJsonArray

                        for ((index, string) in values.withIndex()) {
                            when {
                                index > 0 && index < values.size() - 1 ->
                                    singleErrorMessage.append(", ")
                                index > 0 && index == values.size() - 1 ->
                                    singleErrorMessage.append(" and ")
                            }
                            singleErrorMessage.append(string.asString)
                        }
                    }

                    //reflection to set errors at appropriate field
                    try {
                        field = address::class.java.getDeclaredField(key + "Error")
                        field.isAccessible = true
                        field.set(address, singleErrorMessage.toString())
                        field.isAccessible = false
                        address.notifyChange()
                    } catch (e: Exception) {

                    }
                }
            }
        } catch (e: Exception) {
        }
    }
}