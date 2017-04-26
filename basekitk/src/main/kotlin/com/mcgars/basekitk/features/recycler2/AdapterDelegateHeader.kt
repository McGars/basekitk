package com.mcgars.basekitk.features.recycler2

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import java.util.*

/**
 * Created by gars on 20.04.2017.
 */
open class AdapterDelegateHeader<T>(
        private val items: MutableList<T>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val manager = AdapterDelegatesManager<MutableList<T>>()

    private val headers = mutableListOf<T>()
    private val footers = mutableListOf<T>()

    override fun getItemCount() = headers.size + items.size + footers.size
    val count
        get() = items.size

    fun addDelegate(deletate: AdapterDelegate<MutableList<T>>) {
        manager.addDelegate(deletate)
    }

    fun removeDelegate(deletate: AdapterDelegate<MutableList<T>>) {
        manager.removeDelegate(deletate)
    }

    override fun getItemViewType(position: Int): Int {

        return when {
            isHeader(position) -> manager.getItemViewType(headers, position)
            isFooter(position) ->
                manager.getItemViewType(footers, position - headers.size - items.size)
            else -> manager.getItemViewType(items, position - headers.size)
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
            = manager.onBindViewHolder(items, position, holder)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = manager.onCreateViewHolder(parent, viewType)

    override fun onViewRecycled(holder: RecyclerView.ViewHolder)
            = manager.onViewRecycled(holder)

    override fun onFailedToRecycleView(holder: RecyclerView.ViewHolder)
            = manager.onFailedToRecycleView(holder)

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder)
            = manager.onViewAttachedToWindow(holder)

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder)
            = manager.onViewDetachedFromWindow(holder)

    /**
     * Show before items, on top
     * before add delegate [addDelegate] for this type
     */
    fun addHeader(header: T) {
        if (!headers.contains(header)) {
            headers.add(header)
            //animate
            notifyItemInserted(headers.size - 1)
        }
    }

    /**
     * Remove and notify adapter
     */
    fun removeHeader(header: T) {
        if (headers.contains(header)) {
            notifyItemRemoved(headers.indexOf(header))
            headers.remove(header)
        }
    }

    /**
     * Show in bottom's list
     * before add delegate [addDelegate] for this type
     */
    fun addFooter(footer: T) {
        if (!footers.contains(footer)) {
            footers.add(footer)
            //animate
            notifyItemInserted(headers.size + items.size + footers.size - 1)
        }
    }

    //remove a footer from the adapter
    fun removeFooter(footer: T) {
        if (footers.contains(footer)) {
            //animate
            notifyItemRemoved(headers.size + items.size + footers.indexOf(footer))
            footers.remove(footer)
        }
    }

    fun removeItemByPosition(position: Int) {
        val fixPos = position + headers.size
        items.removeAt(position)
        notifyItemRemoved(fixPos)
    }

    fun removeItem(item: T) {
        val position = items.indexOf(item)
        if (position >= 0) {
            val fixPos = position + headers.size
            items.removeAt(position)
            notifyItemRemoved(fixPos)
        }
    }

    fun notifyItem(position: Int) {
        val fixPos = position + headers.size
        notifyItemChanged(fixPos)
    }

    fun notifyItemsInserted(position: Int, offset: Int = -1) {
        val fixPos = position + headers.size
        if (offset >= 1) {
            notifyItemRangeInserted(fixPos, offset + headers.size)
        } else
            notifyItemInserted(fixPos)
    }

    fun notifyItem(item: T) {
        val position = items.indexOf(item)
        if (position >= 0) {
            val fixPos = position + headers.size
            notifyItemChanged(fixPos)
        }
    }

    fun getItemPosition(item: T) = items.indexOf(item)

    fun addItem(item: T) {
        items.add(item)
        val position = items.indexOf(item)
        val fixPos = position + headers.size
        notifyItemInserted(fixPos)
    }

    fun addItem(position: Int, item: T) {
        val fixPos = position + headers.size
        items.add(position, item)
        notifyItemInserted(fixPos)
    }

    fun swap(firstPosition: Int, secondPosition: Int) {
        Collections.swap(items, firstPosition, secondPosition)
        notifyItemMoved(firstPosition, secondPosition)
    }

    fun getItem(position: Int) = items[position]

    fun isHeaderOrFooter(position: Int) = when {
        isHeader(position) -> true
        isFooter(position) -> true
        else -> false
    }

    private fun isHeader(position: Int) = position >= 0 && position < headers.size
    private fun isFooter(position: Int)
            = position >= 0 && position > items.size && position < itemCount
}