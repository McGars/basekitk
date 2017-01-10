package com.mcgars.basekitk.features.recycler

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import com.mcgars.basekitk.features.recycler.HeaderRecyclerAdapter.Companion.TYPE_FOOTER
import com.mcgars.basekitk.features.recycler.HeaderRecyclerAdapter.Companion.TYPE_HEADER
import com.mcgars.basekitk.features.recycler.HeaderRecyclerAdapter.Companion.TYPE_ITEM

/**
 * Created by Владимир on 22.09.2015.
 */
abstract class BaseRecycleViewGridController(args: Bundle? = null) : BaseRecycleViewController(args) {

    val columnCount: Int
        get() = 2

    //number of columns of the grid
//    override fun initLayoutManager(): LinearLayoutManager {
//        val ml = GridLayoutManager(activity, columnCount)
//        ml.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
//            override fun getSpanSize(position: Int): Int {
//
//                if (getAdapter() == null)
//                    return 1
//
//                return when (getAdapter()?.getItemViewType(position)) {
//                    TYPE_HEADER, TYPE_FOOTER -> 2
//                    TYPE_ITEM -> 1
//                    else -> -1
//                }
//            }
//        }
//        return ml
//    }
    override fun initLayoutManager(): LinearLayoutManager {
        val ml = GridLayoutManager(activity, columnCount)
        ml.spanSizeLookup = AutoSpanSizeLookup(columnCount)
        return ml
    }

    fun setAutoSpan(mCount: Int) {
        with(layoutManager as GridLayoutManager) {
            if (spanCount != mCount) {
                val position = findFirstCompletelyVisibleItemPosition()
                spanCount = mCount
                spanSizeLookup = AutoSpanSizeLookup(mCount)
                recyclerView?.adapter = getAdapter()
                recyclerView?.scrollToPosition(position)
            }
        }

    }

    fun calculateGrid(itemWidth: Int) {
        setAutoSpan(getOptimalColumnsCount(itemWidth))
    }

    fun setCustomLayoutManager(customLayoutManager: LinearLayoutManager) {
        layoutManager = customLayoutManager
        recyclerView?.layoutManager = customLayoutManager
    }

    fun getOptimalColumnsCount(itemWidth: Int): Int {
        val width = resources?.displayMetrics?.widthPixels?.toFloat() ?: 0f
        val padding = recyclerView?.run { paddingLeft + paddingRight } ?: 1
        val count = Math.round(width / (itemWidth + padding))
        return if (count == 0) 1 else count
    }

    inner class AutoSpanSizeLookup(internal val mCount: Int) : GridLayoutManager.SpanSizeLookup() {

        override fun getSpanSize(position: Int): Int {
            if (getAdapter() == null)
                return 1

            return when (getAdapter()?.getItemViewType(position)) {
                TYPE_HEADER, TYPE_FOOTER -> 2
                TYPE_ITEM -> mCount
                else -> -1
            }
//            return if (mAdapter.getItemViewType(position) === 0) mCount else 1
        }
    }

}
