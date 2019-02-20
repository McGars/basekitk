package com.mcgars.basekitk.features.recycler2

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.mcgars.basekitk.R
import com.mcgars.basekitk.tools.inflate

/**
 * Loader
 */
class AdapterViewLoader<T : Any>(
        private val rootAdapter: AdapterDelegateHeader<T>,
        private val loaderObject: T
) : AdapterDelegate<T> {

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

    override fun isForViewType(items: List<T>, position: Int): Boolean {
        return items[position] is BottomLoader
    }

    override fun onCreateViewHolder(kitAdapter: KitAdapter<T>, parent: ViewGroup): RecyclerView.ViewHolder {
        return object : RecyclerView.ViewHolder(
                parent.context.inflate(R.layout.basekit_view_loading, parent)
        ) {}
    }

    override fun onBindViewHolder(items: List<T>, position: Int, holder: RecyclerView.ViewHolder, payloads: List<Any>) {

    }

}

interface BottomLoader