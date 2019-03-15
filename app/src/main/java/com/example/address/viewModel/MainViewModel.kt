package com.example.address.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.address.model.Address
import com.example.address.network.ApiClient
import com.example.address.repository.showErrorToast
import com.google.gson.JsonObject
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val addresses by lazy { MutableLiveData<MutableList<Address>>() }

    fun getAddresses(lambda: (e: Throwable?) -> Unit) {
        val observer = ApiClient.apiInterface.getAddresses()
        observer.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<MutableList<Address>> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: MutableList<Address>) {
                    addresses.value = t
                    lambda(null)
                }

                override fun onError(e: Throwable) {
                    showErrorToast(getApplication())
                    lambda(e)
                }

            })
    }

    fun deleteAddress(addressId: String, lambda: (e: Throwable?) -> Unit) {
        val observer = ApiClient.apiInterface.deleteAddress(addressId)
        observer.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<JsonObject> {
                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: JsonObject) {
                    val address = addresses.value?.find { it.id == addressId.toInt() }
                    addresses.value = addresses.value?.apply { remove(address) }
                    lambda(null)
                }

                override fun onError(e: Throwable) {
                    lambda(e)
                }

            })
    }

    fun createAddress(address: Address) {
        addresses.value = addresses.value?.apply { add(address) }
    }

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