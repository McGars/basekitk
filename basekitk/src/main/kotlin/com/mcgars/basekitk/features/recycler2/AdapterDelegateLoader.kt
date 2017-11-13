package com.mcgars.basekitk.features.recycler2

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.mcgars.basekitk.R

/**
 * Loader
 */
class AdapterViewLoader<T>(
        private val rootAdapter: AdapterDelegateHeader<T>,
        private val loaderObject: T) : AdapterDelegate<MutableList<T>> {

    init {
        if (loaderObject !is BottomLoader)
            throw IllegalAccessException("Implement 'BottomLoader' to your object")
        rootAdapter.addDelegate(this)
    }

    /**
     * Add or remove loader in bottom list
     */
    fun showLoader(show: Boolean) {
        if (!show)
            rootAdapter.removeFooter(loaderObject)
        else {
            rootAdapter.addFooter(loaderObject)
        }
    }

    override fun isForViewType(items: MutableList<T>, position: Int): Boolean {
        return items[position] is BottomLoader
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.basekit_view_loading, parent, false)) {}
    }

    override fun onBindViewHolder(items: MutableList<T>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {

    }

}

interface BottomLoader