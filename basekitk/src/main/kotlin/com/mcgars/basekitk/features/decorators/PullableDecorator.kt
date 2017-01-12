package com.mcgars.basekitk.features.decorators

import android.support.annotation.IdRes
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.features.base.DecoratorListener
import com.mcgars.basekitk.tools.WrapperUiTool

/**
 * Created by gars on 09.01.2017.
 */
class PullableDecorator private constructor(
        val viewId: Int,
        var swipeRefreshLayout: SwipeRefreshLayout?,
        val onRefreshListener: SwipeRefreshLayout.OnRefreshListener) : DecoratorListener() {

    /**
     * Id view which become attached to swipe
     */
    constructor(@IdRes viewId: Int, onRefreshListener: SwipeRefreshLayout.OnRefreshListener)
            : this(viewId, null, onRefreshListener)

    /**
     * Swipe refresh view
     */
    constructor(swipeRefreshLayout: SwipeRefreshLayout, onRefreshListener: SwipeRefreshLayout.OnRefreshListener)
            : this(0, swipeRefreshLayout, onRefreshListener)

    override fun onViewInited(view: View) {
        if (viewId != 0) {
            val v = view.findViewById(viewId) ?: return
            swipeRefreshLayout = SwipeRefreshLayout(view.context)
            swipeRefreshLayout!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            WrapperUiTool(v, swipeRefreshLayout!!).insert()
        }

        swipeRefreshLayout?.setOnRefreshListener(onRefreshListener)
    }

    fun showLoader(show: Boolean) = with(swipeRefreshLayout) {
        this?.post({ isRefreshing = show })
    }

    override fun preDestroyView(controller: Controller, view: View) {
        swipeRefreshLayout?.run {
            setOnRefreshListener(null)
            isRefreshing = false
            destroyDrawingCache()
            clearAnimation()
        }
    }
}