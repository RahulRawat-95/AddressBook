package com.example.address.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.address.model.Address
import com.example.address.network.ApiClient
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

/**
 * View Model class for AddEditAddressActivity
 *
 * @property application the application object
 */
class AddEditAddressViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var address: Address

    fun setAddresss(address: Address) {
        this.address = address
    }

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
                    lambda(e, null)
                }

            })
    }
}