package com.mcgars.basekitk.features.decorators

import androidx.annotation.IdRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.mcgars.basekitk.tools.find

/**
 * Created by Владимир on 17.01.2017.
 * if you not use [com.mcgars.basekitk.features.recycler.BaseRecycleViewController]
 * yuo may use this decorator for load more items when list reached to the end
 */
class MoreLoadDecorator(@IdRes val recyclerView: Int, val listener: (page: Int) -> Unit) : DecoratorListener() {

    internal var page = 0

    var isLoading = false
        private set

    private var hasMoreItems: Boolean = true

    override fun onViewCreated(view: View) {
        view.find<RecyclerView>(recyclerView)?.apply {
            addOnScrollListener(getLoadingListener())
        }
    }

    /**
     * Indicate more load
     */
    fun hasMore(hasMore: Boolean) {
        hasMoreItems = hasMore
        isLoading = false
    }

    /**
     * Set first page
     */
    fun setDefault() {
        page = 0
    }

    private fun getLoadingListener(): RecyclerView.OnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager ?: return

            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()

            val lastVisibleItem = visibleItemCount + pastVisiblesItems
            if (!isLoading && hasMoreItems) {
                if (lastVisibleItem != totalItemCount)
                    return
                page++
                isLoading = true
                listener(page)
            }
        }
    }

}