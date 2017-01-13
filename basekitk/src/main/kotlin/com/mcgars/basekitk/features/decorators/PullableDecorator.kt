package com.mcgars.basekitk.features.decorators

import android.support.annotation.ColorInt
import android.support.annotation.IdRes
import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.mcgars.basekitk.R
import com.mcgars.basekitk.features.base.DecoratorListener
import com.mcgars.basekitk.tools.WrapperUiTool
import com.mcgars.basekitk.tools.color
import com.mcgars.basekitk.tools.find

/**
 * Created by gars on 09.01.2017.
 * Extends view and add pullable behavior
 */
class PullableDecorator private constructor(
        val viewId: Int,
        var swipeLayoutId: Int,
        val onRefreshListener: SwipeRefreshLayout.OnRefreshListener) : DecoratorListener() {

    var swipeRefreshLayout: SwipeRefreshLayout? = null
    /**
     * Id view which become attached to swipe
     */
    constructor(@IdRes viewId: Int, onRefreshListener: SwipeRefreshLayout.OnRefreshListener)
            : this(viewId, 0, onRefreshListener)
    /**
     * Id swipeLayout which swipe behavior
     */
    constructor(@IdRes swipeLayoutId: Int, refreshListener: ()->Unit)
            : this(0, swipeLayoutId, SwipeRefreshLayout.OnRefreshListener {refreshListener()})

    fun setColor(@ColorInt color: Int) {
        swipeRefreshLayout?.setColorSchemeColors(color)
    }

    override fun onViewInited(view: View) {
        if (viewId != 0) {
            val v = view.findViewById(viewId) ?: return
            swipeRefreshLayout = SwipeRefreshLayout(view.context)
            swipeRefreshLayout!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            WrapperUiTool(v, swipeRefreshLayout!!).insert()
        } else if (swipeLayoutId != 0) {
            swipeRefreshLayout = view.find(swipeLayoutId)
        }

        swipeRefreshLayout?.apply {
            setOnRefreshListener(onRefreshListener)
            setColorSchemeColors(context.color(R.attr.colorAccent ))
        }

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