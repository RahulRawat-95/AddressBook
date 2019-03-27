package com.example.address.activity

import androidx.appcompat.app.AppCompatActivity
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseActivity : AppCompatActivity() {
    /**
     * @property mCompositeDisposable the composite disposable to dispose all disposables at once after activity destroys
     */
    private val mCompositeDisposable by lazy { CompositeDisposable() }

    /**
     * Method that adds disposables to Composite Disposable for disposing at the end of activity lifecycle
     */
    protected fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        mCompositeDisposable.dispose()
        super.onDestroy()
    }
}