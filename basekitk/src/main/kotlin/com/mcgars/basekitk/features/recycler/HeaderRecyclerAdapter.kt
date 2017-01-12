package com.mcgars.basekitk.features.recycler

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import com.mcgars.basekitk.R
import java.util.*

abstract class HeaderRecyclerAdapter<T, H : RecyclerView.ViewHolder>(
        val context: Context,
        protected val items: MutableList<T>,
        private val layout: Int,
        var onItemClickListener: ((item: T, position: Int)->Unit)? = null) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    /**
     * Inflater
     */
    protected val inflater: LayoutInflater by lazy { LayoutInflater.from(context) }
    /**
     * Headers views
     */
    internal var headers: MutableList<View> = ArrayList()
    /**
     * Footer views
     */
    internal var footers: MutableList<View> = ArrayList()
    /**
     * Click for items
     */
    protected var itemClickListener: View.OnClickListener = View.OnClickListener { v ->
        val vh = v.getTag(R.id.item_position) as RecyclerView.ViewHolder
        val fixPos = vh.adapterPosition - headers.size

        if (fixPos >= 0 && items.size > fixPos)
            onItemClickListener?.invoke(items[fixPos], fixPos)
    }

    abstract fun getViewHolder(var1: View): H

    /**
     * Call's when holder created
     */
    protected open fun setListeners(holder: H) {}

    /**
     * For few layouts
     */
    protected fun getLayout(viewType: Int): Int {
        return this.layout
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, type: Int): RecyclerView.ViewHolder? {
        //if our position is one of our items (this comes from getItemViewType(int position) below)
        if (type == TYPE_HEADER || type == TYPE_FOOTER) {
            //create a new framelayout, or inflate from a resource
            //make sure it fills the space
            return HeaderFooterViewHolder(FrameLayout(viewGroup.context).apply {
                layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
            })
        } else if (type == TYPE_ITEM) {
            val view = inflater.inflate(getLayout(type), viewGroup, false)
            onItemClickListener?.let { view.setOnClickListener(itemClickListener) }
            return getViewHolder(view).apply { setListeners(this) }
        }

        return null
    }

    /**
     * Override default click
     */
    fun setOriginItemClick(itemClickListener: View.OnClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun onBindViewHolder(vh: RecyclerView.ViewHolder, position: Int) {
        //check what type of view our position is
        if (position < headers.size) {
            val v = headers[position]
            //add our view to a header view and display it
            prepareHeaderFooter(vh as HeaderFooterViewHolder, v)
        } else if (position >= headers.size + items.size) {
            val v = footers[position - items.size - headers.size]
            //add oru view to a footer view and display it
            prepareHeaderFooter(vh as HeaderFooterViewHolder, v)
        } else {
            //it's one of our items, display as required
            val fixPos = position - headers.size
            val item = items[fixPos]
            vh.itemView.setTag(R.id.item_position, vh)
            setValues(vh as H, item, fixPos)
        }
    }

    /**
     * make sure the adapter knows to look for all our items, headers, and footers
     */
    override fun getItemCount() = headers.size + items.size + footers.size

    /**
     * Get original size
     */
    open fun getCount() = headers.size + items.size

    private fun prepareHeaderFooter(vh: HeaderFooterViewHolder, view: View) {
        //empty out our FrameLayout and replace with our header/footer
        vh.base.removeAllViews()
        val parent = view.parent as ViewGroup
        parent?.removeAllViews()
        vh.base.addView(view)
    }

    /**
     * Call's for set values from item to holder
     */
    abstract fun setValues(h: H, item: T, position: Int)

    override fun getItemViewType(position: Int): Int {
        //check what type our position is, based on the assumption that the order is headers > items > footers
        if (position < headers.size) {
            return TYPE_HEADER
        } else if (position >= headers.size + items.size) {
            return TYPE_FOOTER
        }
        return TYPE_ITEM
    }

    //add a header to the adapter
    fun addHeader(header: View) {
        if (!headers.contains(header)) {
            headers.add(header)
            //animate
            notifyItemInserted(headers.size - 1)
        }
    }

    //remove a header from the adapter
    fun removeHeader(header: View) {
        if (headers.contains(header)) {
            //animate
            notifyItemRemoved(headers.indexOf(header))
            headers.remove(header)
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

    fun notifyItem(item: T) {
        val position = items.indexOf(item)
        if (position >= 0) {
            val fixPos = position + headers.size
            notifyItemChanged(fixPos)
        }
    }

    fun getItemPosition(item: T): Int {
        return items.indexOf(item)
    }

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

    //add a footer to the adapter
    fun addFooter(footer: View) {
        if (!footers.contains(footer)) {
            footers.add(footer)
            //animate
            notifyItemInserted(headers.size + items.size + footers.size - 1)
        }
    }

    //remove a footer from the adapter
    fun removeFooter(footer: View) {
        if (footers.contains(footer)) {
            //animate
            notifyItemRemoved(headers.size + items.size + footers.indexOf(footer))
            footers.remove(footer)
        }
    }

    fun swap(firstPosition: Int, secondPosition: Int) {
        Collections.swap(items, firstPosition, secondPosition)
        notifyItemMoved(firstPosition, secondPosition)
    }

    //our header/footer RecyclerView.ViewHolder is just a FrameLayout
    class HeaderFooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var base: FrameLayout

        init {
            this.base = itemView as FrameLayout
        }
    }

    fun getItem(position: Int) = items[position]

    companion object {
        val TYPE_HEADER = 111
        val TYPE_FOOTER = 222
        val TYPE_ITEM = 333
    }
}