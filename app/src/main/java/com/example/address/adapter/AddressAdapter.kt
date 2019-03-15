package com.example.address.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.address.R
import com.example.address.model.Address
import com.example.address.repository.defaultAddressId

class AddressAdapter(
    val context: Context,
    var addresses: MutableList<Address>?,
    var deleteLambda: (Address) -> Unit = {},
    var editLambda: (Address) -> Unit = {}
) :
    RecyclerView.Adapter<AddressAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        return ViewHolder(inflater.inflate(R.layout.item_address, parent, false))
    }

    override fun getItemCount() = addresses?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = addresses?.let { it[position] } ?: return
        holder.addressLine1.text = address.address1
        holder.addressLine2.text = address.address2
        holder.addressLine3.text = address.zipCode

        holder.popupMenu.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            popupMenu.inflate(R.menu.menu_address)
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.edit_address -> {
                        editLambda(address)
                        true
                    }
                    R.id.delete_address -> {
                        deleteLambda(address)
                        true
                    }
                    else -> false
                }
            }
        }

        holder.defaultCheck.visibility = if (address.id == defaultAddressId) View.VISIBLE else View.INVISIBLE

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val addressLine1: TextView = itemView.findViewById(R.id.tv_address_line_1)
        val addressLine2: TextView = itemView.findViewById(R.id.tv_address_line_2)
        val addressLine3: TextView = itemView.findViewById(R.id.tv_address_line_3)
        val popupMenu: ImageView = itemView.findViewById(R.id.iv_popup_menu)
        val defaultCheck: ImageView = itemView.findViewById(R.id.iv_default_address)
    }
}