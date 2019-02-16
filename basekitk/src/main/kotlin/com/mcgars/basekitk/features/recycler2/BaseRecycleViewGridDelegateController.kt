package com.mcgars.basekitk.features.recycler2

import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * Created by Владимир on 22.09.2015.
 */
abstract class BaseRecycleViewGridDelegateController(args: Bundle? = null) : BaseRecycleViewDelegateController(args) {

    open val columnCount: Int
        get() = 2

    override fun initLayoutManager(): LinearLayoutManager {
        val ml = GridLayoutManager(activity, columnCount)
        ml.spanSizeLookup = AutoSpanSizeLookup()
        return ml
    }

    fun setAutoSpan(mCount: Int) {
        with(recyclerView?.layoutManager as GridLayoutManager) {
            if (spanCount != mCount) {
                val position = findFirstCompletelyVisibleItemPosition()
                spanCount = mCount
                spanSizeLookup = AutoSpanSizeLookup()
                recyclerView?.adapter = getAdapter()
                recyclerView?.scrollToPosition(position)
            }
        }

    }

    fun calculateGrid(itemWidth: Int) {
        setAutoSpan(getOptimalColumnsCount(itemWidth))
    }

    fun setCustomLayoutManager(customLayoutManager: LinearLayoutManager) {
        recyclerView?.layoutManager = customLayoutManager
    }

    fun getOptimalColumnsCount(itemWidth: Int): Int {
        val width = resources?.displayMetrics?.widthPixels?.toFloat() ?: 0f
        val padding = recyclerView?.run { paddingLeft + paddingRight } ?: 1
        val count = Math.round(width / (itemWidth + padding))
        return if (count == 0) 1 else count
    }

    inner class AutoSpanSizeLookup : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            if (getAdapter() is AdapterDelegateHeader<*>) {
                return if ((getAdapter() as AdapterDelegateHeader<*>).isHeaderOrFooter(position))
                    2 else 1
            }
            return 1
        }
    }

}
