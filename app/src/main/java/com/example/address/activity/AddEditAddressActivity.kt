package com.example.address.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.address.R
import com.example.address.databinding.ActivityAddEditAddressBinding
import com.example.address.model.Address
import com.example.address.repository.defaultAddressId
import com.example.address.repository.putDefaultAddressId
import com.example.address.repository.showErrorToast
import com.example.address.viewModel.AddEditAddressViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_add_edit_address.*

/**
 * @property EXTRA_ADDRESS constant value for passing address through Intent
 */
const val EXTRA_ADDRESS = "addressToEdit"

/**
 * @property EXTRA_RESULT_ADDRESS constant value for passing address as a result through Intent
 */
const val EXTRA_RESULT_ADDRESS = "resultAddress"

/**
 * @property EXTRA_IS_UPDATE constant value used as key for storing if the address is being updated or created
 */
const val EXTRA_IS_UPDATE = "isUpdateCase"

class AddEditAddressActivity : AppCompatActivity(), View.OnClickListener {

    /**
     * @property mIsUpdate for checking whether the activity was opened for adding new address or editing an existing address
     */
    var mIsUpdate: Boolean = false

    /**
     * @property mAddEditAddressViewModel the ViewModel associated with this Activity
     */
    val mAddEditAddressViewModel by lazy {
        ViewModelProviders.of(this@AddEditAddressActivity).get(AddEditAddressViewModel::class.java)
    }

    /**
     * @property mAddress the Address either passed through Intent or created in this Activity
     */
    lateinit var mAddress: Address

    /**
     * @property mDefaultAddressCb the Checkbox reference in this activity's layout for making specific address default
     */
    val mDefaultAddressCb: CheckBox by lazy { findViewById<CheckBox>(R.id.cb_default_address) }

    /**
     * @property mProgressBar the ProgressBar content layout's reference in the layout for showing progress bar and disabling touches until processing finishes when
     * user submits
     */
    val mProgressBar by lazy { findViewById<FrameLayout>(R.id.fl_loading) }

    /**
     * @property mSubmitButton the submit button that creates or updates an address
     */
    val mSubmitButton by lazy { findViewById<ImageButton>(R.id.imageButton) }

    /**
     * @property mResultCallback the lambda callback to be called by ViewModel when the processing for creation or updating of address finishes successfully
     * @param t Throwable reference that is thrown if some error occurs while calling the Api
     * @param address Address reference that was created or updated
     */
    val mResultCallback = { t: Throwable?, address: Address? ->
        mProgressBar.visibility = View.GONE
        //If Error occurred during api call
        if (t != null)
            showErrorToast(this@AddEditAddressActivity, t)
        else {
            //if default address is checked then the default address id is changed
            if (mDefaultAddressCb.isChecked)
                defaultAddressId = address?.id ?: defaultAddressId
            //if default address is unchecked when the address currently shown was the default address before
            else if (defaultAddressId == address?.id)
                defaultAddressId = -1

            putDefaultAddressId(defaultAddressId)

            //put extras in intent and finish the activity
            val intent = Intent()
            intent.putExtra(EXTRA_RESULT_ADDRESS, address)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            mAddress = intent
                ?.let {
                    mIsUpdate = true
                    it.getSerializableExtra(EXTRA_ADDRESS) as? Address
                }
                ?: let {
                    mIsUpdate = false
                    Address(-1)
                }
        } else {
            mIsUpdate = savedInstanceState.getBoolean(EXTRA_IS_UPDATE)
            mAddress = savedInstanceState.getSerializable(EXTRA_ADDRESS) as Address
        }

        //binding for the Layout for the variable mAddress
        val binding: ActivityAddEditAddressBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_add_edit_address)

        binding.address = mAddress
        binding.defaultAddressID = defaultAddressId

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mSubmitButton.setOnClickListener(this)

        tiet_pincode.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    mSubmitButton.callOnClick()
                    true
                }
                else -> false
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.imageButton -> {
                //do not let the user action commend if all the required fields were not set
                if (!mAddress.isFormValid()) {
                    Toast.makeText(this@AddEditAddressActivity, R.string.error_form_invalid, Toast.LENGTH_LONG)
                        .apply { setGravity(Gravity.CENTER, 0, 0) }
                        .show()
                    return
                }
                mProgressBar.visibility = View.VISIBLE

                //call view model's function according to mIsUpdate
                with(mAddress) {
                    if (mIsUpdate) {
                        mAddEditAddressViewModel.updateAddress(
                            this,
                            lambda = mResultCallback
                        )
                    } else {
                        mAddEditAddressViewModel.createAddress(
                            this,
                            lambda = mResultCallback
                        )
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(EXTRA_ADDRESS, mAddress)
        outState.putSerializable(EXTRA_IS_UPDATE, mIsUpdate)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                if (mProgressBar.visibility != View.VISIBLE)
                    finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}