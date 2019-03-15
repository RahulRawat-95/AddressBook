package com.example.address.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
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
import com.example.address.repository.showErrorToast
import com.example.address.viewModel.AddEditAddressViewModel

const val EXTRA_ADDRESS = "addressToEdit"
const val EXTRA_RESULT_ADDRESS = "resultAddress"

class AddEditAddressActivity : AppCompatActivity() {
    var mIsUpdate: Boolean = false

    val mAddEditAddressViewModel by lazy {
        ViewModelProviders.of(this@AddEditAddressActivity).get(AddEditAddressViewModel::class.java)
    }

    val mAddress: Address by lazy {
        intent
            ?.let {
                mIsUpdate = true
                it.getSerializableExtra(EXTRA_ADDRESS) as? Address
            }
            ?: let {
                mIsUpdate = false
                Address(-1)
            }
    }

    val mDefaultAddressCb: CheckBox by lazy { findViewById<CheckBox>(R.id.cb_default_address) }

    val mProgressBar by lazy { findViewById<FrameLayout>(R.id.fl_loading) }

    val mResultCallback = { t: Throwable?, address: Address? ->
        mProgressBar.visibility = View.GONE
        if (t != null)
            showErrorToast(this@AddEditAddressActivity)
        else {
            if (mDefaultAddressCb.isChecked)
                defaultAddressId = address?.id ?: defaultAddressId
            else if (defaultAddressId == address?.id)
                defaultAddressId = -1

            val intent = Intent()
            intent.putExtra(EXTRA_RESULT_ADDRESS, address)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)

        val binding: ActivityAddEditAddressBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_add_edit_address)

        binding.address = mAddress

        setSupportActionBar(findViewById(R.id.toolbar))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<ImageButton>(R.id.imageButton).setOnClickListener {
            if (!mAddress.isFormValid()) {
                Toast.makeText(this@AddEditAddressActivity, R.string.error_form_invalid, Toast.LENGTH_LONG)
                    .apply { setGravity(Gravity.CENTER, 0, 0) }
                    .show()
                return@setOnClickListener
            }
            mProgressBar.visibility = View.VISIBLE

            with(mAddress) {
                if (mIsUpdate) {
                    mAddEditAddressViewModel.updateAddress(
                        id.toString(),
                        firstName,
                        address1,
                        address2,
                        city,
                        stateName,
                        zipCode,
                        lambda = mResultCallback
                    )
                } else {
                    mAddEditAddressViewModel.createAddress(
                        firstName,
                        address1,
                        address2,
                        city,
                        stateName,
                        zipCode,
                        lambda = mResultCallback
                    )
                }
            }
        }
    }

}