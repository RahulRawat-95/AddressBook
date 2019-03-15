package com.example.address.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.address.model.Address
import com.example.address.network.ApiClient
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class AddEditAddressViewModel(application: Application) : AndroidViewModel(application) {
    fun createAddress(
        firstName: String,
        address1: String,
        address2: String,
        city: String,
        stateName: String? = "Delhi",
        zipCode: String,
        countryId: Int = 105,
        stateId: Int = 1400,
        phone: String = 9875648521.toString(),
        lambda: (e: Throwable?, id: Address?) -> Unit
    ) {
        val observer = ApiClient.apiInterface.createAddress(
            firstName,
            address1,
            address2,
            city,
            stateName ?: "Delhi",
            zipCode,
            countryId,
            stateId,
            phone
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

    fun updateAddress(
        addressId: String,
        firstName: String,
        address1: String,
        address2: String,
        city: String,
        stateName: String? = "Delhi",
        zipCode: String,
        countryId: Int = 105,
        stateId: Int = 1400,
        phone: String = 9875648521.toString(),
        lambda: (e: Throwable?, id: Address?) -> Unit
    ) {
        val observer = ApiClient.apiInterface.updateAddress(
            addressId,
            firstName,
            address1,
            address2,
            city,
            stateName ?: "Delhi",
            zipCode,
            countryId,
            stateId,
            phone
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