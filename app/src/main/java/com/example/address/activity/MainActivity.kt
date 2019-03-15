package com.example.address.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.address.R
import com.example.address.adapter.AddressAdapter
import com.example.address.model.Address
import com.example.address.repository.showErrorToast
import com.example.address.ui.EmptyRecyclerView
import com.example.address.viewModel.MainViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

const val REQUEST_CREATE_ADDRESS = 1
const val REQUEST_UPDATE_ADDRESS = 2

class MainActivity : AppCompatActivity() {

    lateinit var mRecyclerView: EmptyRecyclerView
    lateinit var mEmptyView: View
    lateinit var mMainView: View
    lateinit var mLoadingView: View

    var mIsFetchingAddress = false
    var mIsEmptyViewVisible = true

    val mMainViewModel by lazy { ViewModelProviders.of(this@MainActivity).get(MainViewModel::class.java) }

    val addressAdapter by lazy {
        AddressAdapter(
            this@MainActivity,
            mMainViewModel.addresses.value,
            this@MainActivity::deleteAddress,
            this@MainActivity::editAddress
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        observe()

        mRecyclerView = findViewById(R.id.addresses_rv)
        mEmptyView = findViewById(R.id.layout_empty_view)
        mMainView = findViewById(R.id.layout_addresses)
        mLoadingView = findViewById(R.id.fl_loading)

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<FloatingActionButton>(R.id.fab_add_address).setOnClickListener {
            startActivityForResult(
                Intent(this@MainActivity, AddEditAddressActivity::class.java),
                REQUEST_CREATE_ADDRESS
            )
        }

        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            startActivityForResult(
                Intent(this@MainActivity, AddEditAddressActivity::class.java),
                REQUEST_CREATE_ADDRESS
            )
        }

        mRecyclerView.run {
            setCallback { emptyViewVisible ->
                var fromView: View? = null
                var toView: View? = null
                when {
                    emptyViewVisible && mIsEmptyViewVisible -> return@setCallback
                    !emptyViewVisible && !mIsEmptyViewVisible -> return@setCallback
                    emptyViewVisible -> {
                        fromView = mMainView
                        toView = mEmptyView
                    }
                    !emptyViewVisible -> {
                        fromView = mEmptyView
                        toView = mMainView
                    }
                }

                mIsEmptyViewVisible = emptyViewVisible

                toView!!.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(1f)
                        .setDuration(100L)
                }

                fromView!!.animate()
                    .alpha(0f)
                    .setDuration(100L)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            fromView.visibility == View.GONE
                        }
                    })
            }
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = addressAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_refresh -> {
                if (mIsFetchingAddress)
                    return true
                mIsFetchingAddress = true
                mMainViewModel.getAddresses { mIsFetchingAddress = false }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observe() {
        val addresses = mMainViewModel.addresses.value
        if (!mIsFetchingAddress && (addresses == null || addresses.size == 0)) {
            mIsFetchingAddress = true
            mMainViewModel.getAddresses { mIsFetchingAddress = false }
        }

        mMainViewModel.addresses.observe(this@MainActivity, object : Observer<MutableList<Address>> {
            override fun onChanged(t: MutableList<Address>?) {
                addressAdapter.addresses = t
                addressAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun deleteAddress(address: Address) {
        mLoadingView.visibility = View.VISIBLE
        mMainViewModel.deleteAddress(address.id.toString()) {
            mLoadingView.visibility = View.GONE
            if (it != null)
                showErrorToast(this@MainActivity)
        }
    }

    private fun editAddress(address: Address) {
        val intent = Intent(this@MainActivity, AddEditAddressActivity::class.java)
        intent.putExtra(EXTRA_ADDRESS, address)
        startActivityForResult(intent, REQUEST_UPDATE_ADDRESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CREATE_ADDRESS -> {
                    val address = data?.getSerializableExtra(EXTRA_RESULT_ADDRESS) as? Address
                    address?.let {
                        mMainViewModel.createAddress(address)
                        return
                    }
                }

                REQUEST_UPDATE_ADDRESS -> {
                    val address = data?.getSerializableExtra(EXTRA_RESULT_ADDRESS) as? Address
                    address?.let {
                        mMainViewModel.updateAddress(address)
                        return
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
