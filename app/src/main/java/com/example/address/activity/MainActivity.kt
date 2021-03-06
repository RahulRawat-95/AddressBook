package com.example.address.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.address.R
import com.example.address.adapter.AddressAdapter
import com.example.address.connectivityListener.InternetListener
import com.example.address.connectivityListener.InternetNetworkCallback
import com.example.address.connectivityListener.InternetReceiver
import com.example.address.model.Address
import com.example.address.repository.isPreLollipop
import com.example.address.repository.showErrorToast
import com.example.address.ui.EmptyRecyclerView
import com.example.address.viewModel.MainViewModel
import com.google.gson.JsonObject
import io.reactivex.observers.DisposableSingleObserver
import kotlinx.android.synthetic.main.layout_addresses.*
import kotlinx.android.synthetic.main.layout_addresses_empty_view.*


/**
 * @property REQUEST_CREATE_ADDRESS request code int for Creating Address
 */
const val REQUEST_CREATE_ADDRESS = 1

/**
 * @property REQUEST_UPDATE_ADDRESS request code int for Updating Address
 */
const val REQUEST_UPDATE_ADDRESS = 2

class MainActivity : BaseActivity(), View.OnClickListener {

    /**
     * @property mRecyclerView reference to RecyclerView in the Layout
     */
    private lateinit var mRecyclerView: EmptyRecyclerView
    /**
     * @property mEmptyView reference to Empty View for mRecyclerView's usage when list it is showing is empty
     */
    private lateinit var mEmptyView: View
    /**
     * @property mMainView reference to Main View that holds mRecyclerView to show when recycler view is not empty
     */
    private lateinit var mMainView: View
    /**
     * @property mLoadingView reference to the ProgressBar content layout's reference in the layout for showing progress bar and disabling touches until processing finishes when
     * user deletes some address
     */
    private lateinit var mLoadingView: View

    /**
     * @property mIsFetchingAddress boolean that stores if the api that fetches the Addresses list is being called or not currently
     */
    private var mIsFetchingAddress = false
    /**
     * @property mIsEmptyViewVisible boolean that stores if the empty view for recycler view is currently being shown or not
     */
    private var mIsEmptyViewVisible = true

    /**
     * @property mMainViewModel the View Model associated with this activity
     */
    private val mMainViewModel by lazy { ViewModelProviders.of(this@MainActivity).get(MainViewModel::class.java) }

    /**
     * @property mAddressAdapter the Adapter for mRecyclerView
     */
    private val mAddressAdapter by lazy {
        AddressAdapter(
            this@MainActivity,
            mMainViewModel.addresses.value,
            this@MainActivity::popupMenuShow
        )
    }

    /**
     * @property mInternetListener the listener that will adjust according to the devices api level and will listen to network connection and disconnection calls
     */
    private val mInternetListener: InternetListener by lazy {
        if (isPreLollipop())
            InternetReceiver()
        else
            InternetNetworkCallback()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        enableInternetListener()

        observe()

        mRecyclerView = findViewById(R.id.addresses_rv)
        mEmptyView = findViewById(R.id.layout_empty_view)
        mMainView = findViewById(R.id.layout_addresses)
        mLoadingView = findViewById(R.id.fl_loading)

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        fab_add_address.setOnClickListener(this)

        fab_add.setOnClickListener(this)

        mRecyclerView.run {
            //set callback for recycler view to call when it senses the change in no of items in list
            setCallback { emptyViewVisible ->
                //animate from view
                var fromView: View? = null
                //animate to view
                var toView: View? = null
                when {
                    //branch that checks if the view was already empty then returns
                    emptyViewVisible && mIsEmptyViewVisible -> return@setCallback
                    //branch that checks if the view was already not empty then returns
                    !emptyViewVisible && !mIsEmptyViewVisible -> return@setCallback
                    //branch for animating to empty view
                    emptyViewVisible -> {
                        fromView = mMainView
                        toView = mEmptyView
                    }
                    //branch for animating from empty view
                    !emptyViewVisible -> {
                        fromView = mEmptyView
                        toView = mMainView
                    }
                }

                //change the global mIsEmptyViewVisible
                mIsEmptyViewVisible = emptyViewVisible

                //apply fade in animation to toView
                toView!!.apply {
                    alpha = 0f
                    visibility = View.VISIBLE
                    animate()
                        .alpha(1f)
                        .setDuration(100L)
                        .setListener(null)
                }

                //apply fade out animation to fromView
                fromView!!.animate()
                    .alpha(0f)
                    .setDuration(100L)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            fromView.visibility = View.GONE
                        }
                    })
            }
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = mAddressAdapter

            val dividerItemDecoration = DividerItemDecoration(
                context,
                (layoutManager as LinearLayoutManager).orientation
            )

            addItemDecoration(dividerItemDecoration)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_refresh -> {
                //return if already fetching address
                if (mIsFetchingAddress)
                    return true
                mIsFetchingAddress = true

                fetchAddresses()

                //mMainViewModel.getAddresses { mIsFetchingAddress = false }
                return true
            }
            android.R.id.home -> {
                if (mLoadingView.visibility != View.VISIBLE)
                    finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * method that puts an observer on LiveData provided by ViewModel and calls api if needed
     *
     * @return Unit
     */
    private fun observe() {
        val addresses = mMainViewModel.addresses.value
        //call api if addresses has size 0
        if (!mIsFetchingAddress && (addresses == null || addresses.size == 0)) {
            mIsFetchingAddress = true
            fetchAddresses()
        }

        //observe the LiveData and refresh recycler view when change occurs
        mMainViewModel.addresses.observe(this@MainActivity, Observer<MutableList<Address>> { t: MutableList<Address>? ->
            mAddressAdapter.addresses = t
            mAddressAdapter.notifyDataSetChanged()
        })
    }

    /**
     * method that deletes the Address when delete is clicked on specific Address
     *
     * @param address Address reference to be deleted
     *
     * @return Unit
     */
    private fun deleteAddress(address: Address) {
        mLoadingView.visibility = View.VISIBLE
        mIsFetchingAddress = true
        addDisposable(
            mMainViewModel.deleteAddress(address.id.toString())
                .subscribeWith(DeleteSingleObserver())
        )
    }

    /**
     * method that edits the Address when edit is clicked on specific Address
     *
     * @param address Address reference to be edited
     *
     * @return Unit
     */
    private fun editAddress(address: Address) {
        val intent = Intent(this@MainActivity, AddEditAddressActivity::class.java)
        intent.putExtra(EXTRA_ADDRESS, address)
        startActivityForResult(intent, REQUEST_UPDATE_ADDRESS)
    }

    /**
     * method that shows popup menu over item whose popup icon was clicked
     *
     * @param view Item whose popup icon was clicked
     * @param address Address resembled by the item view which was clicked
     *
     * @return Unit
     */
    private fun popupMenuShow(view: View, address: Address) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.menu_address)
        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit_address -> {
                    editAddress(address)
                    true
                }
                R.id.delete_address -> {
                    deleteAddress(address)
                    true
                }
                else -> false
            }
        }
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

    /**
     * Method that calls get address api to refresh list of addresses and adds the disposable to composite disposable
     *
     * @return Unit
     */
    private fun fetchAddresses() {
        addDisposable(
            mMainViewModel.getAddresses()
                .subscribeWith(FetchSingleObserver())
        )
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_add_address, R.id.fab_add -> {
                startActivityForResult(
                    Intent(this@MainActivity, AddEditAddressActivity::class.java),
                    REQUEST_CREATE_ADDRESS
                )
            }
        }
    }

    override fun onDestroy() {
        disableInternetListener()
        super.onDestroy()
    }

    /**
     * Method that enables Internet Connectivity Listener based on Device Android SDK version
     *
     * @return Unit
     */
    private fun enableInternetListener() {
        mInternetListener.enable(this)
    }

    /**
     * Method that disables Internet Connectivity Listener based on Device Android SDK version
     *
     * @return Unit
     */
    private fun disableInternetListener() {
        mInternetListener.disable(this)
    }

    /**
     * @property DeleteSingleObserver the inner class for single observer for delete api calls
     */
    private inner class DeleteSingleObserver : DisposableSingleObserver<JsonObject>() {
        override fun onSuccess(t: JsonObject) {
            mIsFetchingAddress = false
            mLoadingView.visibility = View.GONE
        }

        override fun onError(e: Throwable) {
            mIsFetchingAddress = false
            mLoadingView.visibility = View.GONE
            showErrorToast(this@MainActivity, e)
        }
    }

    /**
     * @property FetchSingleObserver the inner class for single observer for get addresses api calls
     */
    private inner class FetchSingleObserver : DisposableSingleObserver<MutableList<Address>>() {
        override fun onSuccess(t: MutableList<Address>) {
            mIsFetchingAddress = false
        }

        override fun onError(e: Throwable) {
            mIsFetchingAddress = false
            showErrorToast(application, e)
        }
    }

}