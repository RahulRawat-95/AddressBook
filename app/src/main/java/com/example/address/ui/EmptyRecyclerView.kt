package com.example.address.ui

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

class EmptyRecyclerView : RecyclerView {

    lateinit var emptyViewCallback: (Boolean) -> Unit

    private val observer = object : AdapterDataObserver() {
        override fun onChanged() {
            checkIfEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }
    }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(
        context: Context, attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle) {
    }

    fun checkIfEmpty() {
        if (adapter != null) {
            val emptyViewVisible = adapter!!.itemCount == 0

            emptyViewCallback(emptyViewVisible)
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        val oldAdapter = getAdapter()
        oldAdapter?.unregisterAdapterDataObserver(observer)
        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)

        checkIfEmpty()
    }

    fun setCallback(lambda: (Boolean) -> Unit) {
        emptyViewCallback = lambda
        checkIfEmpty()
    }
}