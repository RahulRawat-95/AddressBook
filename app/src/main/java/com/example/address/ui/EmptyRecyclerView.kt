package com.example.address.ui

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView

/**
 * A class that extends RecyclerView to add functionality for showing empty view in case of empty list
 */
class EmptyRecyclerView : RecyclerView {

    /**
     * @property emptyViewCallback the callback that will be called whenever the recycler view's underlying list count changes
     *
     * @param Boolean which tells if the empty view should be shown or not
     *
     * @return Unit
     */
    lateinit var emptyViewCallback: (Boolean) -> Unit

    /**
     * observer on adapter of this recycler view that will observe when items in the adapter changes
     */
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

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(
        context: Context, attrs: AttributeSet,
        defStyle: Int
    ) : super(context, attrs, defStyle)

    /**
     * method that checks if the recycler view is empty or not and calls the emptyViewCallback based on the result
     *
     * @return Unit
     */
    fun checkIfEmpty() {
        if (adapter != null) {
            val emptyViewVisible = adapter!!.itemCount == 0

            emptyViewCallback(emptyViewVisible)
        }
    }

    override fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        val oldAdapter = getAdapter()
        //unregister old adapter from observer
        oldAdapter?.unregisterAdapterDataObserver(observer)

        super.setAdapter(adapter)
        adapter?.registerAdapterDataObserver(observer)

        checkIfEmpty()
    }

    /**
     * setter for emptyViewCallback
     *
     * @return Unit
     */
    fun setCallback(lambda: (Boolean) -> Unit) {
        emptyViewCallback = lambda
        checkIfEmpty()
    }
}