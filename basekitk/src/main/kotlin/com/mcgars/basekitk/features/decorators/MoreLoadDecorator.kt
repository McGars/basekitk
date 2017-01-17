package com.mcgars.basekitk.features.decorators

import android.support.annotation.IdRes
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.mcgars.basekitk.features.base.DecoratorListener
import com.mcgars.basekitk.tools.find

/**
 * Created by Владимир on 17.01.2017.
 * if you not use [com.mcgars.basekitk.features.recycler.BaseRecycleViewController]
 * yuo may use this decorator for load more items when list reached to the end
 */
class MoreLoadDecorator(@IdRes val recyclerView: Int, val listener: (page: Int)->Unit) : DecoratorListener(){

    internal var page = 0

    var isLoading = false
        private set

    private var hasMoreItems: Boolean = true

    override fun onViewInited(view: View) {
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

            if(!(recyclerView.layoutManager is LinearLayoutManager))
                return

            val visibleItemCount = recyclerView.layoutManager.childCount
            val totalItemCount = recyclerView.layoutManager.itemCount
            val pastVisiblesItems = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

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