package com.mcgars.basekitk.features.recycler

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.mcgars.basekitk.R

/**
 * Created by Владимир on 22.09.2015.
 * Adapter with loader view, for pagination
 */
abstract class ListRecycleAdapter<T, H : RecyclerView.ViewHolder>(
        context: Context,
        items: MutableList<T>,
        layout: Int,
        onItemClickListener: ((item: T, position: Int) -> Unit)? = null) :
        HeaderRecyclerAdapter<T, H>(context, items, layout, onItemClickListener) {

    private var loaderView: View? = null

    init {
        initLoader()
    }

    protected open fun initLoader() {
        loaderView = inflater.inflate(R.layout.basekit_view_loading, null)
        addFooter(loaderView!!)
    }

    fun showLoader(loader: Boolean) {
        loaderView?.visibility = if (loader) View.VISIBLE else View.GONE
    }
}
