package com.example.address.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.address.model.Address
import com.example.address.network.ApiClient
import com.google.gson.JsonObject
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * View Model class for MainActivity
 *
 * @property application the application object
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    /**
     * @property addresses Stores the live data that stores the list of addresses fetched from the api
     */
    val addresses by lazy { MutableLiveData<MutableList<Address>>() }

    /**
     * @property addressesObservable the observable for stream of list of addresses
     */
    private val addressesObservable by lazy {
        ApiClient.apiInterface.getAddresses().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterSuccess { addresses.value = it }
    }

    /**
     * method that returns an asynchronous stream for retrieval of list of addresses
     *
     * @return Single<MutableList<Address>>
     */
    fun getAddresses(): Single<MutableList<Address>> {
        return addressesObservable
    }

    /**
     * method that deletes an address
     *
     * @param addressId the id of the address to be deleted
     *
     * @return Single<JsonObject>
     */
    fun deleteAddress(addressId: String): Single<JsonObject> {
        return ApiClient.apiInterface.deleteAddress(addressId).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doAfterSuccess {
                val address = addresses.value?.find { it.id == addressId.toInt() }
                addresses.value = addresses.value?.apply { remove(address) }
            }
    }

    /**
     * method that updates the address list held by addresses and adds a new address to it and thereby calls any observers observing the Live Data
     *
     * @param address the address object to be added
     *
     * @return Unit
     */
    fun createAddress(address: Address) {
        addresses.value = addresses.value?.apply { add(0,address) }
    }

    /**
     * method that updates the address list held by addresses and updates an existing address to it and thereby calls any observers observing the Live Data
     *
     * @param address the address object to be updated
     *
     * @return Unit
     */
    fun updateAddress(address: Address) {
        var index: Int = -1
        addresses.value?.forEachIndexed { i: Int, addressLocal: Address ->
            if (addressLocal.id == address.id) {
                index = i
                return@forEachIndexed
            }
        }
        if (index > -1)
            addresses.value = addresses.value?.apply { set(index, address) }
    }
}