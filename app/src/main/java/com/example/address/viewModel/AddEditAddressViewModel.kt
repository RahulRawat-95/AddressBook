package com.example.address.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.address.model.Address
import com.example.address.network.ApiClient
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import java.lang.reflect.Field

/**
 * View Model class for AddEditAddressActivity
 *
 * @property application the application object
 */
class AddEditAddressViewModel(application: Application) : AndroidViewModel(application) {

    val jsonParser by lazy { JsonParser() }

    /**
     * Method that creates an Address and calls the api
     *
     * @param address the address to be created
     * @param lambda the callback to be called when api returns either successfully or fails with throwable or address respectively
     */
    fun createAddress(
        address: Address,
        lambda: (e: Throwable?, id: Address?) -> Unit
    ) {
        val observer = ApiClient.apiInterface.createAddress(
            address.firstName,
            address.address1,
            address.address2,
            address.city,
            address.stateName,
            address.zipCode
        )
        observer.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Address> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Address) {
                    lambda(null, t)
                }

                override fun onError(e: Throwable) {
                    handleError(address, e)
                    lambda(e, null)
                }

            })
    }

    /**
     * Method that updates an Address and calls the api
     *
     * @param address the address to be updated
     * @param lambda the callback to be called when api returns either successfully or fails with throwable or address respectively
     */
    fun updateAddress(
        address: Address,
        lambda: (e: Throwable?, id: Address?) -> Unit
    ) {
        val observer = ApiClient.apiInterface.updateAddress(
            address.id.toString(),
            address.firstName,
            address.address1,
            address.address2,
            address.city,
            address.stateName,
            address.zipCode
        )
        observer.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<Address> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: Address) {
                    lambda(null, t)
                }

                override fun onError(e: Throwable) {
                    handleError(address, e)
                    lambda(e, null)
                }

            })
    }

    private fun handleError(address: Address, e: Throwable) {
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

    }
}